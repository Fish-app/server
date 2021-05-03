package no.fishapp.user.client;


import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.util.exceptionmappers.RestClientExceptionMapper;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "authClient")
@RegisterProvider(RestClientExceptionMapper.class)
@Path("/api/auth/authentication/")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface AuthClient extends AutoCloseable, AuthBaseClientInterface {


    @POST
    @Path("newuser")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<AuthenticatedUser> addAuthUser(NewAuthUserData newAuthUserData) throws RestClientHttpException;
}
