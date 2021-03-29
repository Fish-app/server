package no.fishapp.store.commodity.boundary;


import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import no.fishapp.store.commodity.control.CommodityService;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.commodity.DTO.CommodityDTO;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.stream.Collectors;

@Path("commodity")
@Transactional
public class CommodityResource {

    @Inject
    CommodityService commodityService;

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@RolesAllowed({Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity(
            IMultipartBody multipartBody
            //@FormDataParam("name") String name, FormDataMultiPart photo
    ) {
        Response response = null;
        try {
            Commodity commodity = commodityService.addNewCommodity(multipartBody);
            response = Response.ok(commodity).build();
        } catch (IOException | MultipartNameNotFoundException | MultipartReadException e) {
            response = Response.ok().status(Response.Status.BAD_REQUEST).build();
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
