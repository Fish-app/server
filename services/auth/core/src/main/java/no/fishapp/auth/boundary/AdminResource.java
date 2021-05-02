package no.fishapp.auth.boundary;


import no.fishapp.auth.control.AdminService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.AdminChangePasswordData;
import no.fishapp.auth.model.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
public class AdminResource {

    @Inject
    AdminService adminService;


    /**
     * Return all current {@link AuthenticatedUser}
     *
     * @return a list of all current auth users
     */
    @GET
    @Path("all")
    public Response getAllUsers() {
        List<AuthenticatedUser> users = adminService.getAllAuthUsers();
        return Response.ok(users).build();
    }

    /**
     * change the password for the {@link AuthenticatedUser} with the id defined in the provided {@link AdminChangePasswordData}
     *
     * @param adminChangePasswordData the {@link AdminChangePasswordData} containing the user id and the new password
     * @return http 200 if successful
     */
    @PATCH
    @Path("changepassword")
    public Response changePassword(
            AdminChangePasswordData adminChangePasswordData) {
        Response.ResponseBuilder resp;
        boolean sucsess = adminService
                .changeUserPassword(adminChangePasswordData.getUserId(), adminChangePasswordData.getNewPassword());
        if (!sucsess) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok();
        }
        return resp.build();
    }

}
