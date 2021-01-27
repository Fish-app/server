package no.maoyi.app.listing.boundry;


import no.maoyi.app.listing.control.ListingService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("buy-order")
public class ListingResource {

    @Inject
    ListingService listingService;

    @POST
    @Path("new")
    public Response newBuyOrder(
            @FormDataParam("title") String title,
            @FormDataParam("desk") String description,
            @FormDataParam("osv...") int dopdido,
            FormDataMultiPart photos
    ) {

        return Response.ok().build();

    }
}
