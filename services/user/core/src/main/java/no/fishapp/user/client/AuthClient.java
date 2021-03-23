package no.fishapp.user.client;


import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "authClient")
@Path("/auth")
public interface AuthClient extends AutoCloseable {

    @POST
    @Path("newUser")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<AuthenticatedUser> addAuthUser(UsernamePasswordData usernamePasswordData,
                                                          List<String> groups
    );
}
