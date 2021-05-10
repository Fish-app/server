package no.fishapp.app.auth.boundry;


import no.fishapp.app.auth.control.AuthenticationService;
import no.fishapp.app.auth.control.KeyService;
import no.fishapp.app.auth.entity.AuthenticatedUser;
import no.fishapp.app.auth.entity.DTO.UserChangPasswordData;
import no.fishapp.app.auth.entity.DTO.UserLoginData;
import no.fishapp.app.auth.entity.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("auth/authentication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {


    @Inject
    AuthenticationService authService;

    @Inject
    KeyService keyService;


    /**
     * Authenticates a user if providing a correct email/password combination.
     * Returns a JWT token on success else an error response.
     *
     * @param userLoginData the user login data
     *
     * @return JSON Response
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            UserLoginData userLoginData
    ) {


        Response.ResponseBuilder response;

        try {
            String email = userLoginData.getUserName().toLowerCase();

            AuthenticatedUser user = authService.getUserFromPrincipal(email);
            if (user == null) {
                response = Response.ok().status(Response.Status.UNAUTHORIZED);
            } else {
                CredentialValidationResult result = authService.getValidationResult(user.getId(),
                                                                                    userLoginData.getPassword()

                );
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


    @PATCH
    @Path("changepass")
    @Valid
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response changePassword(
            UserChangPasswordData changPasswordData
    ) {
        if (authService.changePassword(changPasswordData.getNewPassword(), changPasswordData.getOldPassword())) {
            return Response.ok().build();
        } else {
            return Response.ok().status(Response.Status.FORBIDDEN).build();
        }
    }

}
