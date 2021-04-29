package no.fishapp.store.commodity.boundary;


import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import no.fishapp.store.commodity.control.CommodityService;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;

/**
 * Manages all HTTP requests that are about {@link Commodity}.
 */
@Path("commodity")
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommodityResource {

    @Inject
    CommodityService commodityService;

    /**
     *  Add a new {@link Commodity} from the provided {@link IMultipartBody}.
     * @param multipartBody the {@link IMultipartBody} containing the information about the {@code Commodity}
     * that is to be created
     * @return a {@link Response} containing the new {@code Commodity} if successful, {@link Response.Status#BAD_REQUEST} if not
     */
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

    /**
     * Return all {@link Commodity}.
     * @return a {@link Response} with all the commodities
     */
    @GET
    @Path("all")
    public Response getAllCommodities() {
        return Response.ok(commodityService.getAllCommodities()).build();
    }

    /**
     * Return a single instance of each {@link Commodity} with an active {@link Listing}.
     * @return a {@link Response} with all the commodities
     */
    @GET
    @Path("all-display")
    public Response getAllDisplayCommodities() {
        return Response.ok(commodityService.getAllDisplayCommodities()).build();
    }

    /**
     * Return a single {@link Commodity} with an id matching the id argument.
     * @param id the id of the {@code Commodity} to be found
     * @return a {@link Response} with the {@code Commodity} if one is found, {@link Response.Status#BAD_REQUEST} if not
     */
    @GET
    @Path("{id}")
    public Response getSingleCommodities(
            @PathParam("id") long id
    ) {
        Optional<Commodity> commodity = commodityService.getCommodity(id);

        return commodity.map(Response::ok)
                        .orElse(Response.ok().status(Response.Status.BAD_REQUEST))// todo: kansje bedre med 404
                        .build();

    }
}
