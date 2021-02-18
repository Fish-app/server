package no.***REMOVED***.app.auth.boundry;

import no.***REMOVED***.app.auth.control.AdminService;
import no.***REMOVED***.app.auth.entity.Group;
import no.***REMOVED***.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.BUYER_GROUP_NAME})
public class AdminResource {

    @Inject
    AdminService adminService;

    @PATCH
    @Path("changepass")
    public Response changePassword(
            @HeaderParam("userid") int userId,
            @HeaderParam("newpass") String newpass
    ) {
        Response.ResponseBuilder resp;
        boolean                  sucsess = adminService.changeUserPassword(userId, newpass);
        if (! sucsess) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok();
        }
        return resp.build();
    }
}
