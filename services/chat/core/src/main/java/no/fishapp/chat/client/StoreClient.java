package no.fishapp.chat.client;


import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.util.restClient.auth.AuthBaseClientInterface;
import no.fishapp.util.restClient.exceptionHandlers.RestClientHttpException;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "storeClient")
@Path("/api/store/")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface StoreClient extends AutoCloseable, AuthBaseClientInterface {

    @GET
    @Path("listing/listing/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CompletionStage<ChatListingInfo> getListing(@PathParam("id") long listingId) throws RestClientHttpException;
}
