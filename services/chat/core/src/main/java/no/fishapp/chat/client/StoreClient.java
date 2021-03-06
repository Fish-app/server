package no.fishapp.chat.client;


import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "storeClient")
@Path("/api/store/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface StoreClient extends AutoCloseable, AuthBaseClientInterface {

    /**
     * This client is used to send a HTTP request to the store microservices-component to associate a
     * conversation with the requested listing. A custom type {@link ChatListingInfo}is used to hold
     * the information.
     *
     * @param listingId The ID of the listing
     * @return A {@link ChatListingInfo} object encapsulated inside a {@link CompletionStage}.
     * @throws RestClientHttpException A exception that catches any HTTP failure codes if present
     */
    @GET
    @Path("listing/listing/cli/{id}")
    CompletionStage<ChatListingInfo> getListing(@PathParam("id") long listingId) throws RestClientHttpException;
}
