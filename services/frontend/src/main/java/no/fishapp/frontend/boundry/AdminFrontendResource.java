package no.fishapp.frontend.boundry;


import no.fishapp.auth.model.DTO.AdminChangePasswordData;
import no.fishapp.auth.model.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
//@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
public class AdminFrontendResource {


    @GET
    @Path("test")
    public Response changePassword(

    ) {
        return Response.ok("Ello").build();
    }

}
