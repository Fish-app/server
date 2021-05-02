package no.fishapp.auth.boundary;


import no.fishapp.auth.control.AuthenticationService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UserChangePasswordData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("authentication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {


    @Inject
    AuthenticationService authService;


    /**
     * Returns a Jwt token if the provided {@link UsernamePasswordData} yields a valid login,
     * Returns http 401 response if not.
     *
     * @param usernamePasswordData the {@link UsernamePasswordData} object.
     * @return http 200 with the Json web token in the {@link HttpHeaders#AUTHORIZATION} of the response.
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @NotNull UsernamePasswordData usernamePasswordData) {
        Optional<String> loginToken = authService.getToken(usernamePasswordData);

        return loginToken.map(s -> Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + s))
                         .orElse(Response.ok().status(Response.Status.UNAUTHORIZED)).build();

    }


    /**
     * Change the password for the current logged in {@link AuthenticatedUser} to the values spesified in the provided {@link UserChangePasswordData}
     *
     * @param changPasswordData the {@link UserChangePasswordData} object.
     * @return http 200 on sucsess if wrong old password http 403 is returned
     */
    @PUT
    @Path("changepass")
    @Valid
    public Response changePassword(
            UserChangePasswordData changPasswordData) {
        if (authService.changePassword(changPasswordData.getNewPassword(), changPasswordData.getOldPassword())) {
            return Response.ok().build();
        } else {
            return Response.ok().status(Response.Status.FORBIDDEN).build();
        }
    }


    /**
     * Adds a new {@link AuthenticatedUser} from the provided {@link NewAuthUserData}.
     * This endpoint is used by other services not Users.
     *
     * @param newAuthUserData the {@link NewAuthUserData} object.
     * @return a new auth user object if successful null if not
     */
    @POST
    @Path("newuser")
    @RolesAllowed(value = {Group.CONTAINER_GROUP_NAME})
    public AuthenticatedUser newUser(NewAuthUserData newAuthUserData) {
        if (!authService.isPrincipalInUse(newAuthUserData.getUserName())) {
            return authService.createUser(newAuthUserData).orElse(null);
        }
        return null;


    }

}
