package no.fishapp.util.restClient.auth;

import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.util.restClient.RestClientExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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
