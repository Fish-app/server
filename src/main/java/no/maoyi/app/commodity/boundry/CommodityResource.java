package no.***REMOVED***.app.commodity.boundry;


import no.***REMOVED***.app.user.entity.Group;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commodity")
@Stateless
public class CommodityResource {

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //TODO: REMOVE USER FROM HERE
    @RolesAllowed({Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity(
            @FormDataParam("name") String name,
            FormDataMultiPart photo
    ) {


        return Response.ok().build();
    }
}
