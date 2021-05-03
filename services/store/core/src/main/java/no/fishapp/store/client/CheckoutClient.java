package no.fishapp.store.client;


import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.CompletionStage;


@RegisterRestClient(configKey = "checkoutClient")
@Path("/api/checkout/")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface CheckoutClient extends AutoCloseable, AuthBaseClientInterface {


    @GET
    @Path("subscription/valid/{userid}")
    CompletionStage<Boolean> isUserSubscribed(@PathParam("userid") long userId);
}
