package no.fishapp.checkout.client;


import no.fishapp.checkout.model.dibsapi.BulkSubscriptionCharge;
import no.fishapp.checkout.model.dibsapi.NewSubscription;
import no.fishapp.checkout.model.dibsapi.responses.BulkChargeResponse;
import no.fishapp.checkout.model.dibsapi.responses.SubscriptionResponse;
import no.fishapp.util.exceptionmappers.RestClientExceptionMapper;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@RegisterRestClient(configKey = "dibs")
@RegisterProvider(RestClientExceptionMapper.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public interface DibsPaymentClient {


    @POST
    @Path("/v1/payments/")
    SubscriptionResponse newSubscription(
            @HeaderParam("Authorization") String privateKey,
            NewSubscription newSubscription) throws RestClientHttpException;

    @GET
    @Path("/v1/subscriptions/{subscriptionId}")
    Response getSubscriptionDetails(
            @PathParam("subscriptionId") String subscriptionId,
            @HeaderParam("Authorization") String privateKey) throws RestClientHttpException;


    @POST
    @Path("/v1/payments/{paymentId}/cancels")
    Response cancelPayment(@PathParam("paymentId") String paymentId) throws RestClientHttpException;

    @POST
    @Path("/v1/subscriptions/charges")
    Map<String, String> makeBulkCharge(
            @HeaderParam("Authorization") String privateKey,
            BulkSubscriptionCharge bulkSubscriptionCharge) throws RestClientHttpException;

    @GET
    @Path("/v1/subscriptions/charges/{bulkId}")
    BulkChargeResponse getBulkOrderDetails(
            @PathParam("bulkId") String bulkId,
            @HeaderParam("Authorization") String privateKey,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("pageNumber") long pageNumber) throws RestClientHttpException;

}


