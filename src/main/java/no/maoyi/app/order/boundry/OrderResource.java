package no.***REMOVED***.app.order.boundry;


import no.***REMOVED***.app.order.control.OrderService;
import no.***REMOVED***.app.order.entity.Order;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("order")
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    @Path("new")
    public Response newOrder(
            @FormDataParam("title") String title,
            @FormDataParam("desk")  String description,
            @FormDataParam("osv...")int dopdido,
            FormDataMultiPart photos
    ){
        Order returnOrder = orderService.newOrder();

        return Response.ok(returnOrder).build();

    }
}
