package no.fishapp.store.core.commodity.boundry;


import no.fishapp.app.auth.entity.Group;
import no.fishapp.store.core.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.commodity.entity.DTO.CommodityDTO;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.stream.Collectors;

@Path("commodity")
@Stateless
public class CommodityResource {

    @Inject
    CommodityService commodityService;

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@RolesAllowed({Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity(
            @FormDataParam("name") String name, FormDataMultiPart photo
    ) {
        Response response = null;
        try {
            Commodity commodity = commodityService.addNewCommodity(name, photo
            );
            response = Response.ok(commodity).build();
        } catch (IOException e) {
            // todo: finn en streamlined metode for og sende tilbake status meldinger og få http kodene korrekt
            response = Response.ok().status(Response.Status.BAD_REQUEST).build();
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations()
             .forEach(constraintViolation -> System.out.println(constraintViolation.getConstraintDescriptor()));

        }

        return response;
    }

    @GET
    @Path("all")
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getAllCommoditys() {
        return Response.ok(commodityService.getAllCommodities()).build();
    }

    @GET
    @Path("all-display")
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getAllDisplayCommoditys() {
        return Response.ok(commodityService.getAllCommodities()
                                           .stream()
                                           .map(CommodityDTO::new)
                                           .collect(Collectors.toList())).build();
    }

    @GET
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("{id}")
    public Response getSingleCommoditys(
            @PathParam("id") long id
    ) {
        Commodity commodity = commodityService.getCommodity(id);
        if (commodity == null) {
            return Response.ok().status(Response.Status.BAD_REQUEST).build();
        } else {
            return Response.ok(commodity).build();

        }
    }
}
