package no.***REMOVED***.app.commodity.boundry;


import no.***REMOVED***.app.commodity.control.CommodityService;
import no.***REMOVED***.app.commodity.entity.Commodity;
import no.***REMOVED***.app.user.entity.Group;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("commodity")
@Stateless
public class CommodityResource {

    @Inject
    CommodityService commodityService;

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //TODO: REMOVE USER FROM HERE
    @RolesAllowed({Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity(
            @FormDataParam("name") String name,
            FormDataMultiPart photo
    ) {
        Response response = null;
        try {
            Commodity commodity = commodityService.addNewCommodity(name, photo);
            response = Response.ok(commodity).build();
        } catch (IOException e) {
            // todo: finn en streamlined metode for og sende tilbake status meldinger og få http kodene korrekt
            response = Response.ok().status(Response.Status.BAD_REQUEST).build();
        }

        return response;
    }

    @GET
    @Path("all")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity() {
        return Response.ok(commodityService.getAllCommodities()).build();
    }
}
