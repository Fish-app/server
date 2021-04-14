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

@RegisterRestClient(configKey = "imageClient")
@Path("image")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface ImageClient extends AutoCloseable, AuthBaseClientInterface {

    @PUT
    @Path("new")
    CompletionStage<Image> addImage(
            @HeaderParam("name") String filename,
            @HeaderParam("mimetype") String mimetype,
            InputStream inputStream
    );
}
