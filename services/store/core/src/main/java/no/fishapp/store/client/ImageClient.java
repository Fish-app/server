package no.fishapp.store.client;


import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.util.restClient.auth.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.concurrent.CompletionStage;

/**
 * This client is used to send a HTTP request to the media microservices-component to process an image and save it.
 */
@RegisterRestClient(configKey = "imageClient")
@Path("image")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface ImageClient extends AutoCloseable, AuthBaseClientInterface {

    /**
     * Sends an image to the media microservice-component to be processed.
     * @param filename the filename of the image
     * @param mimetype the mimetype of the image
     * @param inputStream the {@link InputStream} of the image
     * @return an {@link Image} encapsulated inside a {@link CompletionStage}
     */
    @PUT
    @Path("new")
    CompletionStage<Image> addImage(
            @HeaderParam("name") String filename,
            @HeaderParam("mimetype") String mimetype,
            InputStream inputStream
    );
}
