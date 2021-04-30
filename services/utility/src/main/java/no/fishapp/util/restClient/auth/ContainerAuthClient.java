package no.fishapp.util.restClient.auth;

import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.util.restClient.exceptionHandlers.RestClientExceptionMapper;
import no.fishapp.util.restClient.exceptionHandlers.RestClientHttpException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "interContainerAuthClient")
@RegisterProvider(RestClientExceptionMapper.class)
@Path("/api/auth/")
public interface ContainerAuthClient extends AutoCloseable {


    @POST
    @Path("authentication/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UsernamePasswordData usernamePasswordData) throws RestClientHttpException;

    @GET
    @Path("key.pem")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPubKey() throws RestClientHttpException;
}
