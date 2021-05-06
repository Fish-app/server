package no.fishapp.user.boundary;


import lombok.extern.java.Log;
import no.fishapp.user.control.UserService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Log
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Response getAllFromIds(List<Long> userIdList) {
        return Response.ok(userService.getSellersFromIdList(userIdList)).build();
    }


}
