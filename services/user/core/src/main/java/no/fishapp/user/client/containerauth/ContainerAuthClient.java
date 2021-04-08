package no.fishapp.user.client.containerauth;

import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.user.client.RestClientExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;


@RegisterRestClient(configKey = "authClient")
@RegisterProvider(RestClientExceptionMapper.class)
@Path("/")
public interface ContainerAuthClient {


    @POST
    @Path("authentication/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UsernamePasswordData usernamePasswordData);

    @GET
    @Path("key.pem")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPubKey();
}
