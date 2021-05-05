package no.fishapp.store.client;


import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;


@RegisterRestClient(configKey = "userClient")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface StoreClient extends AutoCloseable, AuthBaseClientInterface {


    @GET
    @Path("user")
    CompletionStage<Boolean> isUserSubscribed(List<Long> userIds);
}
