package no.fishapp.checkout.boundary;


import no.fishapp.checkout.control.CheckoutService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class CheckoutResource {

    @Inject
    CheckoutService checkoutService;

    @GET
    @Path("valid")
    @Valid
    public Response isSubscriptionValid(
            long userId
    ) {
return Response.accepted().build();
    }

    @GET
    public Response test(){
        return Response.ok(checkoutService.createNewSubscription()).build();
    }


}
