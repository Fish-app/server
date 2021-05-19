package no.fishapp.checkout.client;


import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.util.exceptionmappers.RestClientExceptionMapper;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "authClient")
@RegisterProvider(RestClientExceptionMapper.class)
@Path("/api/auth/")
public interface AuthClient extends AutoCloseable {


    @POST
    @Path("authentication/login")
    Response getDibsLoginToken(UsernamePasswordData usernamePasswordData) throws RestClientHttpException;
}
