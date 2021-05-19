package no.fishapp.store.client;


import no.fishapp.media.model.Image;
import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.io.InputStream;
import java.util.concurrent.CompletionStage;

/**
 * This client is used to send a HTTP request to the media microservices-component to process an image and save it.
 */
@RegisterRestClient(configKey = "imageClient")
@Path("/api/media/image/")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface ImageClient extends AutoCloseable, AuthBaseClientInterface {

    /**
     * Sends an image to the media microservice-component to be processed.
     *
     * @param filename    the filename of the image
     * @param mimetype    the mimetype of the image
     * @param inputStream the {@link InputStream} of the image
     * @return an {@link Image} encapsulated inside a {@link CompletionStage}
     */
    @PUT
    @Path("new")
    CompletionStage<Image> addImage(
            @HeaderParam("name") String filename, @HeaderParam("mimetype") String mimetype, InputStream inputStream);
}
