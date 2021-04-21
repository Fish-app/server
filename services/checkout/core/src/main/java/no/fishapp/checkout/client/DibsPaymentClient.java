package no.fishapp.checkout.client;

import no.fishapp.checkout.client.exceptionHandlers.RestClientExceptionMapper;
import no.fishapp.checkout.client.exceptionHandlers.RestClientHttpException;
import no.fishapp.checkout.model.DTO.NewSubscription;
import no.fishapp.checkout.model.DTO.SubscriptionResponse;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "dibs")
@RegisterProvider(RestClientExceptionMapper.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public interface DibsPaymentClient {


    @POST
    @Path("/v1/payments/")
    SubscriptionResponse newSubscription(@HeaderParam("Authorization") String privateKey, NewSubscription newSubscription) throws RestClientHttpException;

    @POST
    @Path("/v1/payments/{paymentId}/cancels")
    CompletionStage<Void> cancelPayment(@PathParam("paymentId") String paymentId) throws RestClientHttpException;

    @POST
    @Path("/v1/subscriptions/charges")
    CompletionStage<Void> makeBulkCharge()  throws RestClientHttpException;









}
