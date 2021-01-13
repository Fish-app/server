package no.maoyi.app.user.boundry;

import no.maoyi.app.user.control.KeyManager;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

@Path("key.pem")
public class KeyService {

    @Inject
    private KeyManager keyManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response publicKey() {
        String        key       = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyManager.getPublicKey().getEncoded());
        StringBuilder keyResult = new StringBuilder();
        keyResult.append("-----BEGIN PUBLIC KEY-----\n");
        keyResult.append(key);
        keyResult.append("\n-----END PUBLIC KEY-----");
        return Response.ok(keyResult.toString()).build();
    }

    public KeyManager getKeyManager() {
        return this.keyManager;
    }

}