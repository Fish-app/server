package no.fishapp.user.boundary;

import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.user.control.AdminUserInfoService;
import no.fishapp.user.model.user.Buyer;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
//@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
@Log
public class AdminUserInfoResource {


    @Inject
    AdminUserInfoService adminUserInfoService;

    @GET
    @Path("all")
    public Response getAllUsers() {
        var users = adminUserInfoService.getAllUsers();
        return Response.ok(users).build();
    }


}
