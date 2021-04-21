package no.fishapp.checkout.client;

import no.fishapp.checkout.model.DTO.SubscriptionResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.CompletionStage;

@RegisterRestClient
public interface DibsPaymentClient {


    @POST
    @Path("/v1/payments/")
    CompletionStage<SubscriptionResponse> newSubscription();

    @POST
    @Path("/v1/payments/{paymentId}/cancels")
    CompletionStage<Void> canslePayment(@PathParam("paymentId") String paymentId);

    @POST
    @Path("/v1/subscriptions/charges")
    CompletionStage<Void> makeBulkCharge();

    @POST
    @Path("/v1/payments/{paymentId}/cancels")
    CompletionStage<Void> canslePayment(@PathParam("paymentId") String paymentId);







}
