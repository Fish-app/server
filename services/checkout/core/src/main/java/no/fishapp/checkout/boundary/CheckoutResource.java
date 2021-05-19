package no.fishapp.checkout.boundary;


import no.fishapp.checkout.control.CheckoutService;
import no.fishapp.checkout.exeptions.UserAlreadySubscribedException;
import no.fishapp.checkout.model.dibsapi.responses.SubscriptionResponse;
import no.fishapp.checkout.model.dibsapi.responses.WebhookAnswer;
import no.fishapp.util.exceptionmappers.NoJwtTokenException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Implementes the REST HTTP API for the Checkout-component of the Microservice
 * <p>
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RegisterProvider(NoJwtTokenException.class)
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
    @Path("subscription/status/{uid}")
    @Valid
    public Response getSubscriptionStatus(@PathParam("uid") long userId) {
        return Response.accepted(checkoutService.getSubscriptionStatus(userId)).build();
    }

    @GET
    @Path("subscription/cancel")
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


    // -- payment webhooks -- //


    @POST
    @PermitAll
    @Path("webhooks/payment-created")
    public Response webhookPayCreated(String data) {
        System.out.println("############################################");
        System.out.println("pay created");
        System.out.println(data);
        System.out.println("############################################");
        return Response.ok().build();
    }

    @POST
    @PermitAll
    @Path("webhooks/payment-checkout-completed")
    public Response webhookPayChekoutComplete(String data) {
        System.out.println("############################################");
        System.out.println("chekout complete");
        System.out.println(data);
        System.out.println("############################################");
        return Response.ok().build();
    }

    @POST
    @PermitAll
    @Path("webhooks/payment-reservation-created")
    public Response webhookPayReservationCreated(String data) {
        System.out.println("############################################");
        System.out.println("pay reservation created");
        System.out.println(data);
        System.out.println("############################################");
        return Response.ok().build();
    }


    @POST
    @PermitAll
    @Path("webhooks/payment-charge-created")
    public Response webhookPayChargeCreated(WebhookAnswer webhookAnswer) {
        System.out.println("############################################");
        System.out.println("pay charge created");
        System.out.println(webhookAnswer.getData().getPaymentId());
        System.out.println("############################################");
        checkoutService.chargeSuccessWebhook(webhookAnswer.getData().getPaymentId());
        return Response.ok().build();
    }

    @POST
    @PermitAll
    @Path("webhooks/payment-charge-failed")
    public Response webhookPaychargeFailed(WebhookAnswer webhookAnswer) {
        System.out.println("############################################");
        System.out.println("pay charge failed");
        System.out.println(webhookAnswer);
        System.out.println("############################################");
        checkoutService.chargeFailedWebhook(webhookAnswer.getData().getPaymentId());
        return Response.ok().build();
    }

    // -- health chek -- //

    /**
     * Used to check if up
     *
     * @return
     */
    @GET
    @Path("hello")

    public Response healthCheck(
    ) {
        return Response.ok("Ello").build();
    }


}
