package no.fishapp.frontend.boundry;


import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
//@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
public class AdminFrontendResource {

    // health chek
    @GET
    @Path("test")
    public Response healthCheck(
    ) {
        return Response.ok("Ello").build();
    }

}
