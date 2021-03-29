package no.fishapp.store.client;


import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "imageClient")
@Path("image")
public interface ImageClient extends AutoCloseable {

    @PUT
    @Path("new")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    public CompletionStage<Image> addAuthUser(NewImageDto newImageDto, InputStream fileInputStream);
}
