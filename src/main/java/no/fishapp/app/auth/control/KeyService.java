package no.fishapp.app.auth.control;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import no.fishapp.app.util.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KeyService {
    private static final String KEYPAIR_FILENAME = "jwtkeys.ser";
    private static final File KEYPAIR_FILE = new File("jwtkeys.ser");

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    KeyPair keyPair = null;

    
    /**
     * get the key pair file, if no file is fond or an error reading a new is created
     */
    @PostConstruct
    protected void setKeyPair() {
        if (Files.exists(Paths.get(KEYPAIR_FILENAME))) {
            keyPair = readKeyPair();
        }

        if (keyPair == null) {
            keyPair = createKeyPair();
            writeKeyPair(keyPair);
        }
    }

    private KeyPair readKeyPair() {
        KeyPair result = null;

        try {
            result = FileUtils.deserializeObjetFromFile(KEYPAIR_FILE);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, "Failed to read keyfile", ex);
        }
        return result;
    }

    private void writeKeyPair(KeyPair keyPair) {
        try {
            FileUtils.serializeObjetToFile(keyPair, KEYPAIR_FILENAME);
        } catch (IOException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private KeyPair createKeyPair() {
        return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    public PublicKey getRSAPublic() {
        return keyPair.getPublic();
    }

    public PrivateKey getRSAPrivate() {
        return keyPair.getPrivate();
    }

    public String getPublicKey() {
        String key = Base64.getMimeEncoder(64, "\n".getBytes())
                           .encodeToString(keyPair.getPublic().getEncoded());
        StringBuilder keyResult = new StringBuilder();
        keyResult.append("-----BEGIN PUBLIC KEY-----\n");
        keyResult.append(key);
        keyResult.append("\n-----END PUBLIC KEY-----");


        return keyResult.toString();
    }

    public String generateNewJwtToken(String mail, long userId, Set<String> groups) {
        try {
            Date now        = new Date();
            Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
            JwtBuilder jb = Jwts.builder()
                                .setHeaderParam("typ", "JWT")               // type
                                .setHeaderParam("alg", "RS256")             // algorithm
                                .setHeaderParam("kid", "abc-1234567890")    // key id
                                .setSubject(String.valueOf(userId))
                                .setId(UUID.randomUUID().toString())                    // id
                                .claim("iss", issuer)
                                .setIssuedAt(now)
                                .setExpiration(expiration)
                                .claim("upn", mail) // user principal name
                                .claim("groups", groups)
                                //                       .claim("aud", "aud")
                                //                      .claim("auth_time", now)
                                .signWith(keyPair.getPrivate());
            return jb.compact();
        } catch (InvalidKeyException t) {

        }
        return "";
    }


}