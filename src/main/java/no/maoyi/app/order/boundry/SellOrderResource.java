package no.maoyi.app.order.boundry;


import no.maoyi.app.order.control.OrderService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("sell-order")
public class SellOrderResource {

    @Inject
    OrderService orderService;

    @POST
    @Path("new")
    public Response newSellOrder(
            @FormDataParam("title") String title,
            @FormDataParam("desk") String description,
            @FormDataParam("osv...") int dopdido,
            FormDataMultiPart photos
    ) {

        return Response.ok().build();

    }
}
