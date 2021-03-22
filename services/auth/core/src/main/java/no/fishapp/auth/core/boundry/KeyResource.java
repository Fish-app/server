package no.fishapp.auth.core.boundry;


import no.fishapp.auth.core.control.KeyService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("key.pem")
@Dependent
public class KeyResource {

    @Inject
    private KeyService keyService;

    /**
     * returns the public key for the jwt signing
     *
     * @return a text response with the pub key
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response publicKey() {
        try {
            return Response.ok(keyService.getPublicKey()).build();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }
}
