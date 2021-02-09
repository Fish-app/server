package no.maoyi.app.user.boundry;


import no.maoyi.app.user.control.AuthenticationService;
import no.maoyi.app.user.control.KeyService;
import no.maoyi.app.user.control.UserService;
import no.maoyi.app.user.entity.Group;
import no.maoyi.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("authentication")
public class AuthResource {

    @Inject
    JsonWebToken webToken;

    @Inject
    AuthenticationService authService;

    @Inject
    KeyService keyService;

    @Inject
    UserService userService;
    
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @HeaderParam("email") String email,
            @HeaderParam("password") String password,
            @Context HttpServletRequest request
    ) {

        Response.ResponseBuilder response;
        try {
            User user = userService.getUserFromEmail(email);
            if (user == null) {
                response = Response.ok().status(Response.Status.UNAUTHORIZED);
            } else {
                CredentialValidationResult result = authService.getValidationResult(user.getId(), password);
                if (authService.isAuthValid(result)) {
                    System.out.println("AUTH: Login OK:'" + email + "', UID:" + user.getId().toString());

                    String token = keyService.generateNewJwtToken(email, user.getId(), result.getCallerGroups());
                    response = Response.ok(user).header(HttpHeaders.AUTHORIZATION,
                                                        "Bearer " + token
                    );
                } else {
                    System.out.println("AUTH: Login REJECT :'" + email + "', UID:" + user.getId().toString());
                    response = Response.ok("{}").status(Response.Status.UNAUTHORIZED);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, "Login error", e);
            response = Response.ok("{}")
                               .status(500);
        }

        return response.build();
    }


    /**
     * TODO: what is this is it needed?
     *
     * @return
     */
    @POST
    @Path("credentialTest")
    @RolesAllowed("admin")
    public Response credentialTest() {
        return Response.ok(webToken.getGroups()).build();
    }


    /**
     * Changes the password for a user by setting the email address.
     * Users need to verify their old password, in order to do a change.
     * Administators (Group.ADMIN) can change password for users,
     * without entering the current password of the user.
     *
     * @param emailAccess Email address of user to change password
     * @param newPasswd New password
     * @param oldPasswd Old Password (not required for admins)
     * @param sc
     * @return
     */
    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(
            @HeaderParam("email") String emailAccess,
            @HeaderParam("pwd") String newPasswd,
            @HeaderParam("oldpwd") String oldPasswd,
            @Context SecurityContext sc) {

        if (emailAccess == null || newPasswd == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        StringBuilder logMesg = new StringBuilder();
        logMesg.append("AUTH: Change password for '" + emailAccess + "'");

        User accessUser = userService.getUserFromEmail(emailAccess);
        if (accessUser == null) {
            logMesg.append(" - FAIL (User not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        BigInteger id = accessUser.getId();
        Boolean authorizedToChange = false;

        logMesg.append(" - REQUEST BY (" + id + ", " + accessUser.getEmail() + ") ");


        // The user initiating the password change request (aka the caller)
        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;

        Response.Status state = Response.Status.BAD_REQUEST;

        if ((newPasswd == null || newPasswd.length() < 6)) {
            logMesg.append(" - FAIL(Password unsatisfied)");
        } else {
            // Admin rolegroup has permission to change password for other users (and overrides the checks below)
            if (sc.isUserInRole(Group.ADMIN_GROUP_NAME)) {
                state = Response.Status.OK;
                authorizedToChange = true;

                // 1. Verify caller has RoleGroup.USER
                // 2. Verify caller IS SAME user as will change password to (Zero difference in strings (== 0))
                // 3, Verify that caller has entered old password (admins shall never req usr old passwd!)
            } else if (sc.isUserInRole(Group.USER_GROUP_NAME) && authuser.compareToIgnoreCase(id.toString()) == 0  && oldPasswd != null) {
                CredentialValidationResult result = authService.getValidationResult(id, oldPasswd);

                switch (result.getStatus()) {
                    case VALID:
                        authorizedToChange = true;
                        state = Response.Status.OK;
                        logMesg.append("OK");
                        break;

                    case INVALID:
                        state = Response.Status.FORBIDDEN;
                        logMesg.append("FORBIDDEN");
                        break;

                    case NOT_VALIDATED:
                        // The database or something went horribly wrong
                        state = Response.Status.SERVICE_UNAVAILABLE;
                        break;
                }
            } else {
                state = Response.Status.UNAUTHORIZED;
                logMesg.append("UNAUTHORIZED");
            }
        }

        if (authorizedToChange) {
            authService.ChangePassword(newPasswd);
            state = Response.Status.fromStatusCode(200);
            logMesg.append(" -> Change successful");
        } else {
            logMesg.append(" -> Change aborted");
        }

        System.out.println(logMesg);
        return Response.status(state).build();
    }

}
