package no.***REMOVED***.app.user.boundry;


import no.***REMOVED***.app.user.control.AuthenticationService;
import no.***REMOVED***.app.user.control.KeyService;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
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
    public Response login(
            @HeaderParam("email") String email,
            @HeaderParam("password") String password,
            @Context HttpServletRequest request
    ) {

        Response.ResponseBuilder response;
        try {
            User user = userService.getUserFromEmail(email);
            if (user == null) {
                response = Response.ok("Wrong username / password").status(Response.Status.UNAUTHORIZED);
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
     * Changes the password for the current user to the new one provided
     *
     * @param newPassword the new password
     *
     * @return Statuscode ok if ok 500 if not TODO: change this
     */
    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(@HeaderParam("password") String newPassword) {

        try {
            authService.ChangePassword(newPassword);
            return Response.ok("Successfully changed password").build();
        } catch (Exception e) {
            return Response.ok("Failed to change password").status(500).build();
        }

    }


}
