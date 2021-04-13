package no.fishapp.auth.boundary;


import no.fishapp.auth.control.AdminService;
import no.fishapp.auth.model.DTO.AdminChangePasswordData;
import no.fishapp.auth.model.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
public class AdminResource {

    @Inject
    AdminService adminService;

    @PATCH
    @Path("changepassword")
    public Response changePassword(
            AdminChangePasswordData adminChangePasswordData
    ) {
        Response.ResponseBuilder resp;
        boolean success = adminService.changeUserPassword(
                adminChangePasswordData.getUserId(),
                adminChangePasswordData.getNewPassword()
        );
        if (!success) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok();
        }
        return resp.build();
    }

}
