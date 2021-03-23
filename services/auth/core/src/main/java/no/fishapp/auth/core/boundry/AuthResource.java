package no.fishapp.auth.core.boundry;


import no.fishapp.auth.core.control.AuthenticationService;
import no.fishapp.auth.core.control.KeyService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.UserChangPasswordData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;


import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     *
     * @return JSON Response
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @NotNull UsernamePasswordData usernamePasswordData
    ) {


        Response.ResponseBuilder response;

        try {
            String email = usernamePasswordData.getUserName().toLowerCase();

            AuthenticatedUser user = authService.getUserFromPrincipal(email);
            if (user == null) {
                response = Response.ok().status(Response.Status.UNAUTHORIZED);
            } else {
                CredentialValidationResult result = authService.getValidationResult(user.getId(),
                                                                                    usernamePasswordData.getPassword()

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

    /**
     * Authenticates a user if providing a correct email/password combination.
     * Returns a JWT token on success else an error response.
     *
     * @param usernamePasswordData the user login data
     *
     * @return JSON Response
     */
    @POST
    @Path("newuser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(
            @NotNull UsernamePasswordData usernamePasswordData
    ) {
        AuthenticatedUser user = authService.createUser(usernamePasswordData, List.of());

        if (user == null) {
            // TODO: rett feilkode her?
            return Response.ok().status(Response.Status.NOT_MODIFIED).build();
        } else {
            return Response.ok(user).build();

        }

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
    @Path("newUser")
    public AuthenticatedUser newUser(UsernamePasswordData usernamePasswordData, List<String> groups) {
        if (usernamePasswordData == null) {
            //todo:handle
            System.out.println("isnull aaaaaaaaaaaaaaaaaaaaaaaa");
            return null;
        }
        if (! authService.isPrincipalInUse(usernamePasswordData.getUserName())) {
            return authService.createUser(usernamePasswordData, groups);
        }
        return null;


    }

}
