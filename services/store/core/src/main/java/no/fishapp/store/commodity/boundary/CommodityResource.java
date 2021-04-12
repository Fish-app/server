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
import java.util.Optional;
import java.util.stream.Collectors;

@Path("commodity")
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommodityResource {

    @Inject
    CommodityService commodityService;

    @POST
    @Path("new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@RolesAllowed({Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response addNewCommodity(
            IMultipartBody multipartBody
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
    public Response getAllCommoditys() {
        return Response.ok(commodityService.getAllCommodities()).build();
    }

    @GET
    @Path("all-display")
    public Response getAllDisplayCommoditys() {
        return Response.ok(commodityService.getAllDisplayCommodities()).build();
    }

    @GET
    @Path("{id}")
    public Response getSingleCommoditys(
            @PathParam("id") long id
    ) {
        Optional<Commodity> commodity = commodityService.getCommodity(id);

        return commodity.map(Response::ok)
                        .orElse(Response.ok().status(Response.Status.BAD_REQUEST))// todo: kansje bedre med 404
                        .build();

    }
}
