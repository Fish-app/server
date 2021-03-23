package no.fishapp.user.boundry;


import no.fishapp.auth.model.Group;
import no.fishapp.chat.model.user.User;
import no.fishapp.user.control.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
public class UserResource {

    @Inject
    UserService userService;

    /**
     * Returns the current logged in user
     *
     * @return the current loged in user
     */
    @GET
    @Path("current")
    public Response getCurrentUser() {
        ResponseBuilder resp;
        User            user = userService.getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(user);
        }
        return resp.build();
    }
}
