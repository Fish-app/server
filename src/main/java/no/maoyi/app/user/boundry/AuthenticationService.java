package no.***REMOVED***.app.user.boundry;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;

import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.CredentialValidationResult.Status;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Path("authentication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class AuthenticationService {

    @Inject
    KeyService keyService;

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    @PersistenceContext
    EntityManager em;

    @Inject
    JsonWebToken tk;

    @Inject
    PasswordHash hasher;

    /**
     * Returns the current logged in user
     *
     * @return
     */
    @GET
    @Path("currentuser")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response getCurrentUser() {
        ResponseBuilder resp;
        User            user = getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(user);
        }
        return resp.build();
    }

    /**
     * Returns the logged in user - it gets the user by the email in the JWT name field
     *
     * @return the logged in user or null
     */
    public User getLoggedInUser() {
        try {
            return em.createNamedQuery(User.USER_BY_EMAIL, User.class)
                     .setParameter("email", tk.getName())
                     .getSingleResult();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Authenticates a user if providing a correct email/password combination.
     * Returns a JWT token on success else an error response.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @param request  http request
     *
     * @return JSON Response
     */
    @POST
    @Path("login")
    public Response login(
            @HeaderParam("email") String email,
            @HeaderParam("password") String password,
            @Context HttpServletRequest request
    ) {

        ResponseBuilder response;
        try {
            CredentialValidationResult result = identityStoreHandler
                    .validate(new UsernamePasswordCredential(email, password));
            if (result.getStatus() == Status.VALID) {
                String token = generateToken(email, result.getCallerGroups(), request);
                User   user  = em.createNamedQuery(User.USER_BY_EMAIL, User.class)
                                 .setParameter("email", email)
                                 .getSingleResult();
                System.out.println(user.getEmail());
                response = Response.ok(user).header(HttpHeaders.AUTHORIZATION,
                                                                      "Bearer " + token
                );
            } else {
                response = Response.ok("Wrong username / password");
            }
        } catch (Exception e) {
            response = Response.ok("Unexpected login error")
                               .status(500);
        }

        return response.build();
    }

    /**
     * Creates a new userin the system.
     *
     * @param name     the first name of the user
     * @param username the last name of the user
     * @param password desired password for the user
     * @param email    email for the user
     *
     * @return returns ok, or error if invalid creation
     */
    @POST
    @Path("create")
    public Response createUser(@HeaderParam("name") String name, @HeaderParam("username") String username,
                               @HeaderParam("password") String password, @HeaderParam("email") String email
    ) {


        ResponseBuilder resp;
        try {
            User user = em.createNamedQuery(User.USER_BY_EMAIL, User.class)
                          .setParameter("email", email)
                          .getSingleResult();
            resp = Response.ok(
                   "User already exist, please try another email");
        } catch (NoResultException e) {


            User  newUser   = new User(email, name, username, password);
            Group usergroup = em.find(Group.class, Group.USER_GROUP_NAME);
            newUser.setPassword(hasher.generate(password.toCharArray()));
            newUser.getGroups().add(usergroup);

            em.persist(newUser);


            resp = Response.ok("Successfully created user");
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user")
                           .status(500);
        }
        return resp.build();

    }

    @POST
    @Path("credentialTest")
    @RolesAllowed("admin")
    public Response credentialTest() {
        return Response.ok(tk.getGroups()).build();
    }

    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(@HeaderParam("password") String newPassword) {

        try {
            User user = getLoggedInUser();
            user.setPassword(hasher.generate(newPassword.toCharArray()));
            em.merge(user);
            return Response.ok("Successfully changed password").build();
        } catch (Exception e) {
            return Response.ok("Failed to change password").status(500).build();
        }

    }

    /**
     * Generates a JWT token and returns it, else empty string
     *
     * @param name    name of the subject
     * @param groups  groups to include in the JWT
     * @param request http request
     *
     * @return returns JWT token or empty string
     */
    private String generateToken(String name, Set<String> groups, HttpServletRequest request) {
        try {
            Date now        = new Date();
            Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
            JwtBuilder jb = Jwts.builder()
                                .setHeaderParam("typ", "JWT")
                                .setHeaderParam("kid", "abc-1234567890")
                                .setSubject(name)
                                .setId("a-123")
                                .claim("iss", issuer)
                                .setIssuedAt(now)
                                .setExpiration(expiration)
                                .claim("upn", name)
                                .claim("groups", groups)
                                .claim("aud", "aud")
                                .claim("auth_time", now)
                                .signWith(keyService.getKeyManager().getPrivateKey());
            return jb.compact();
        } catch (InvalidKeyException t) {

        }
        return "";
    }
}
