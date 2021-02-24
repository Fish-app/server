package no.***REMOVED***.app.auth.boundry;


import no.***REMOVED***.app.auth.control.AuthenticationService;
import no.***REMOVED***.app.auth.control.KeyService;
import no.***REMOVED***.app.auth.entity.AuthenticatedUser;
import no.***REMOVED***.app.auth.entity.Group;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("authentication")
public class AuthResource {


    @Inject
    AuthenticationService authService;

    @Inject
    KeyService keyService;


    /**
     * Authenticates a user if providing a correct email/password combination.
     * Returns a JWT token on success else an error response.
     *
     * @param email    the email of the user
     * @param password the password of the user
     *
     * @return JSON Response
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @HeaderParam("email") String email,
            @HeaderParam("password") String password
    ) {

        Response.ResponseBuilder response;
        try {
            email = email.toLowerCase();
            AuthenticatedUser user = authService.getUserFromPrincipal(email);
            if (user == null) {
                response = Response.ok().status(Response.Status.UNAUTHORIZED);
            } else {
                CredentialValidationResult result = authService.getValidationResult(user.getId(), password);
                if (authService.isAuthValid(result)) {
                    System.out.println("AUTH: Login OK:'" + email + "', UID:" + user.getId());

                    String token = keyService.generateNewJwtToken(email, user.getId(), result.getCallerGroups());
                    response = Response.ok(user).header(HttpHeaders.AUTHORIZATION,
                                                        "Bearer " + token
                    );
                } else {
                    System.out.println("AUTH: Login REJECT :'" + email + "', UID:" + user.getId());
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


    @PUT
    @Path("changepassword")
    @Valid
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(
            @NotNull @HeaderParam("pwd") String newPasswd,
            @NotNull @HeaderParam("oldpwd") String oldPasswd
    ) {

        if (!(oldPasswd.isBlank() && newPasswd.isBlank())) {
            if (authService.changePassword(newPasswd, oldPasswd)) {
                return Response.ok().build();
            } else {
                return Response.ok().status(Response.Status.FORBIDDEN).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
