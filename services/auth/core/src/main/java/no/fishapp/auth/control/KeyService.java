package no.fishapp.auth.control;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The type Key service.
 */
@ApplicationScoped
public class KeyService {

    @Inject
    @ConfigProperty(name = "jwt.cert.file", defaultValue = "jwtkeys.ser")
    private String keyPairSaveFile;

    /**
     * The Issuer.
     */
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    private String issuer;

    private KeyPair keyPair = null;

    /**
     * Gets rsa public key.
     *
     * @return the rsa public key
     */
    private PublicKey getRSAPublic() {
        return keyPair.getPublic();
    }

    /**
     * Gets rsa private key.
     *
     * @return the rsa private key
     */
    private PrivateKey getRSAPrivate() {
        return keyPair.getPrivate();
    }

    /**
     * Gets public key.
     *
     * @return the public key
     */
    public String getPublicKey() {
        String key = Base64.getMimeEncoder(64, "\n".getBytes())
                           .encodeToString(keyPair.getPublic().getEncoded());


        return "-----BEGIN PUBLIC KEY-----\n" +
                key +
                "\n-----END PUBLIC KEY-----";
    }

    /**
     * Generate new jwt token string.
     *
     * @param principalName the user principal name
     * @param userId        the user id
     * @param groups        the groups the user is in
     * @return the jwt string
     */
    public String generateNewJwtToken(String principalName, long userId, Set<String> groups) {
        try {

            Instant now            = Instant.now();
            Instant expirationTime = now.plus(1, ChronoUnit.DAYS);
            JwtBuilder jb = Jwts.builder()
                                .setHeaderParam("typ", "JWT")               // type
                                .setHeaderParam("alg", "RS256")             // algorithm
                                .setHeaderParam("kid", "abc-1234567890")    // key id
                                .setSubject(String.valueOf(userId))
                                .setId(UUID.randomUUID().toString())                    // id
                                .claim("iss", issuer)
                                .setIssuedAt(Date.from(now))
                                .setExpiration(Date.from(expirationTime))
                                .claim("upn", principalName)                               // user principal name
                                .claim("groups", groups)
                                .signWith(this.getRSAPrivate());
            return jb.compact();
        } catch (InvalidKeyException ignore) {
        }
        return null;
    }

    /**
     * read a keypair from file
     *
     * @return the keypair if success null if not
     */
    private KeyPair readKeyPair() {
        KeyPair result = null;

        try {
            result = deserializeKeyPair(new File(this.keyPairSaveFile));
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, "Failed to read keyfile", ex);
        }
        return result;
    }

    /**
     * Save the keypair to file
     *
     * @param keyPair the keypair to save
     */
    private void writeKeyPair(KeyPair keyPair) {
        try {
            this.serializeKeyPairToFile(keyPair, keyPairSaveFile);
        } catch (IOException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private KeyPair createKeyPair() {
        return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    /**
     * Tries to deserialize the keypair saved at the provided file location
     *
     * @param file the file to deserialize from.
     * @return the deserialized key pair
     * @throws IOException if error reading the key pair
     */
    private KeyPair deserializeKeyPair(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        Object            readObject        = objectInputStream.readObject();

        if (readObject instanceof KeyPair) {
            return (KeyPair) readObject;
        } else {
            throw new IOException();
        }
    }

    /**
     * Serializes the provided keypair object
     *
     * @param object the keypair to serialize.
     * @param file   the file to serialize to.
     * @throws IOException error writing object
     */
    private void serializeKeyPairToFile(KeyPair object, String file) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(
                file)));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
    }

    /**
     * get the key pair file, if no file is fond or an error reading a new is created
     */
    @PostConstruct
    protected void setKeyPair() {
        if (Files.exists(Paths.get(keyPairSaveFile))) {
            keyPair = readKeyPair();
        }

        if (keyPair == null) {
            keyPair = createKeyPair();
            writeKeyPair(keyPair);
        }
    }


}