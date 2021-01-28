package no.***REMOVED***.app.user.boundry;

import no.***REMOVED***.app.user.control.AuthenticationService;
import no.***REMOVED***.app.user.control.KeyService;
import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.Seller;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("authentication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class UserResource {
    @Inject
    AuthenticationService authService;


    @Inject
    KeyService keyService;


    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken webToken;

    @Inject
    PasswordHash hasher;

    /**
     * Returns the logged in user - it gets the user by the email in the JWT name field
     *
     * @return the logged in user or null
     */
    private User getLoggedInUser() {
        try {
            return entityManager.createNamedQuery(User.USER_BY_EMAIL, User.class)
                                .setParameter("email", webToken.getName())
                                .getSingleResult();
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * Returns the current logged in user
     *
     * @return
     */
    @GET
    @Path("currentuser")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME})
    public Response getCurrentUser() {
        ResponseBuilder resp;
        User            user = authService.getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(user);
        }
        return resp.build();
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
            User user = authService.getUserFromEmail(email);
            if (user == null) {
                response = Response.ok("Wrong username / password").status(Response.Status.UNAUTHORIZED);
            } else {
                CredentialValidationResult result = authService.gerValidationResult(user.getId(), password);
                if (authService.isAuthValid(result)) {

                    String token = keyService.generateNewJwtToken(email, user.getId(), result.getCallerGroups());
                    response = Response.ok(user).header(HttpHeaders.AUTHORIZATION,
                                                        "Bearer " + token
                    );
                } else {
                    response = Response.ok("Wrong username / password").status(Response.Status.UNAUTHORIZED);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, "Login error", e);
            response = Response.ok("Unexpected login error")
                               .status(500);
        }

        return response.build();
    }

    /**
     * Creates a new user in the system.
     *
     * @param name     the first name of the user
     * @param password desired password for the user
     * @param email    email for the user
     *
     * @return returns ok, or error if invalid creation
     */
    @POST
    @Path("create")
    public Response createUser(@HeaderParam("name") String name, @HeaderParam("email") String email,
                               @HeaderParam("password") String password
    ) {
        ResponseBuilder resp;
        try {
            User user = authService.getUserFromEmail(email);
            if (user == null) {

                User newUser = authService.createUser(name, email, password);
                resp = Response.ok(newUser);


            } else {
                resp = Response.ok(
                        "User already exist, please try another email").status(Response.Status.CONFLICT);
            }

        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user").status(500);
        }
        return resp.build();

    }

    @POST
    @Path("createseller")
    public Response createSeller(@HeaderParam("name") String name, @HeaderParam("email") String email,
                                 @HeaderParam("password") String password, @HeaderParam("regNumber") String regNumber
    ) {
        ResponseBuilder resp;
        try {
            Seller seller = authService.getSellerFromEmail(email);
            if (seller == null) {
                Seller newSeller = authService.createSeller(name, email, password, regNumber);
                resp = Response.ok(newSeller);
            } else {
                resp = Response.ok("Seller already exists, please try another email").status(Response.Status.CONFLICT);
            }
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the seller").status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return resp.build();
    }

    @POST
    @Path("credentialTest")
    @RolesAllowed("admin")
    public Response credentialTest() {
        return Response.ok(webToken.getGroups()).build();
    }

    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(@HeaderParam("password") String newPassword) {

        try {
            User user = getLoggedInUser();
            user.setPassword(hasher.generate(newPassword.toCharArray()));
            entityManager.merge(user);
            return Response.ok("Successfully changed password").build();
        } catch (Exception e) {
            return Response.ok("Failed to change password").status(500).build();
        }

    }

//-----------------------------For Testing----------------------------------
    @GET
    @Path("getseller")
    public Response getSeller(@QueryParam("email") String email) {
        ResponseBuilder resp;
        try {
            Seller seller = authService.getSellerFromEmail(email);
            if (seller == null) {
                resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
            } else {
                resp = Response.ok(seller);
            }

        } catch (Exception e) {
            resp = Response.ok("Something went wrong").status(500);
        }
        return resp.build();
    }
}
