package no.***REMOVED***.app.user.boundry;


import no.***REMOVED***.app.user.control.AuthenticationService;
import no.***REMOVED***.app.user.control.KeyService;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.User;
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
            email = email.toLowerCase();
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
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(
            @HeaderParam("email") String emailAccess,
            @HeaderParam("pwd") String newPasswd,
            @HeaderParam("oldpwd") String oldPasswd,
            @Context SecurityContext sc) {

        return authService.validatePasswordChangeRequest(emailAccess, newPasswd, oldPasswd, sc);
    }

}
