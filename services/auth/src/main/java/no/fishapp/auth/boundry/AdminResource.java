package no.fishapp.auth.boundry;

import no.fishapp.auth.entity.DTO.AdminChangePasswordData;
import no.fishapp.auth.entity.Group;
import no.fishapp.auth.control.AdminService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
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
    @Path("changepasswordaa")
    public Response changePasswordaa(
            AdminChangePasswordData adminChangePasswordData
    ) {
        Response.ResponseBuilder resp;
        boolean sucsess = adminService.changeUserPassword(
                adminChangePasswordData.getUserId(),
                adminChangePasswordData.getNewPassword()
        );
        if (!sucsess) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok();
        }
        return resp.build();
    }

}
