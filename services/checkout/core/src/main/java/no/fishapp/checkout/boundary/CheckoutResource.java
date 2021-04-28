package no.fishapp.checkout.boundary;


import no.fishapp.checkout.control.CheckoutService;
import no.fishapp.checkout.exeptions.UserAlreadySubscribedException;
import no.fishapp.checkout.model.dibsapi.responses.SubscriptionResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class CheckoutResource {

    @Inject
    CheckoutService checkoutService;

    @GET
    @Path("subscription/valid/{uid}")
    @Valid
    public Response isSubscriptionValid(@PathParam("uid") long userId) {
        return Response.accepted(checkoutService.isSubscriptionValid(userId)).build();
    }

    @GET
    @Path("subscription/cansle")
    public Response cancelSubscription() {
        return Response.ok().build();
    }

    @GET
    @Path("new-sub")
    public Response newSubscription() {
        try {
            Optional<SubscriptionResponse> retv = checkoutService.newSubscription();
            return retv.map(Response::ok).orElse(Response.serverError()).build();
        } catch (UserAlreadySubscribedException e) {
            return Response.ok("User already subscribed").status(Response.Status.NOT_MODIFIED).build();
        }


    }


}
