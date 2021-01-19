package no.***REMOVED***.app.user.boundry;

import no.***REMOVED***.app.user.control.KeyService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

@Path("key.pem")
public class KeyResource {

    @Inject
    private KeyService keyService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response publicKey() {
        return Response.ok(keyService.getPublicKey()).build();
    }
}
