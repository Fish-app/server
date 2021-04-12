package no.fishapp.auth.boundary;


import no.fishapp.auth.control.AuthenticationService;
import no.fishapp.auth.control.KeyService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UserChangPasswordData;
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
import java.util.List;

@Path("authentication")
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
     * @param usernamePasswordData the user login data
     * @return JSON Response
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @NotNull UsernamePasswordData usernamePasswordData
    ) {


        Response.ResponseBuilder response;


        String loginToken = authService.getToken(usernamePasswordData);

        if (loginToken != null) {
            response = Response.ok().header(HttpHeaders.AUTHORIZATION,
                                            "Bearer " + loginToken);

        } else {
            response = Response.ok().status(Response.Status.UNAUTHORIZED);
        }


        return response.build();
    }




    @PUT
    @Path("changepass")
    @Valid
    public Response changePassword(
            UserChangPasswordData changPasswordData
    ) {
        if (authService.changePassword(changPasswordData.getNewPassword(), changPasswordData.getOldPassword())) {
            return Response.ok().build();
        } else {
            return Response.ok().status(Response.Status.FORBIDDEN).build();
        }
    }


    @POST
    @Path("newuser")
    @RolesAllowed(value = {Group.CONTAINER_GROUP_NAME})
    public AuthenticatedUser newUser(NewAuthUserData newAuthUserData) {
        if (newAuthUserData == null) {
            //todo:handle
            System.out.println("isnull aaaaaaaaaaaaaaaaaaaaaaaa");
            return null;
        }
        if (!authService.isPrincipalInUse(newAuthUserData.getUserName())) {
            return authService.createUser(newAuthUserData);
        }
        return null;


    }

    /**
     * Authenticates a user if providing a correct email/password combination.
     * Returns a JWT token on success else an error response.
     *
     * @param usernamePasswordData the user login data
     * @return JSON Response
     */
//    @POST
//    @Path("newuserdev")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createUser(
//            @NotNull UsernamePasswordData usernamePasswordData
//    ) {
//        AuthenticatedUser user = authService.createUser(usernamePasswordData, List.of());
//
//        if (user == null) {
//            // TODO: rett feilkode her?
//            return Response.ok().status(Response.Status.NOT_MODIFIED).build();
//        } else {
//            return Response.ok(user).build();
//
//        }
//
//    }

}
