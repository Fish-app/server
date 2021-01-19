package no.***REMOVED***.app.order.boundry;


import no.***REMOVED***.app.order.control.OrderService;
import no.***REMOVED***.app.order.entity.BuyBaseOrder;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("buy-order")
public class BuyOrderResource {

    @Inject
    OrderService orderService;

    @POST
    @Path("new")
    public Response newBuyOrder(
            @FormDataParam("title") String title,
            @FormDataParam("desk") String description,
            @FormDataParam("osv...") int dopdido,
            FormDataMultiPart photos
    ) {
        BuyBaseOrder returnBuyOrder = orderService.newBuyOrder();

        return Response.ok(returnBuyOrder).build();

    }
}
