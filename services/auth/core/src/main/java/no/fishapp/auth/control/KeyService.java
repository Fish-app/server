package no.fishapp.auth.control;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;


/**
 * Manages the jwt key and tokens for the aplication.
 * this is done by offering tokens from {@link KeyService#generateNewJwtToken(String, long, Set)} and
 * signed by the {@link PrivateKey} paired with the {@link PublicKey} provided as a object or b64 encoded fron {@link KeyService#getPublicKey()}
 */
@Log
@ApplicationScoped
public class KeyService {

    @Inject
    @ConfigProperty(name = "jwt.cert.file", defaultValue = "jwtkeys.ser")
    private String keyPairSaveFile;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    private String issuer;

    private KeyPair keyPair = null;

    /**
     * Returns the rsa {@link PublicKey}.
     *
     * @return the {@link PublicKey}.
     */
    private PublicKey getRSAPublic() {
        return keyPair.getPublic();
    }

    /**
     * Returns the rsa {@link PrivateKey}.
     *
     * @return the {@link PrivateKey}
     */
    private PrivateKey getRSAPrivate() {
        return keyPair.getPrivate();
    }

    /**
     * Returns the base64 encoded public key String.
     *
     * @return the base64 encoded public key String.
     */
    public String getPublicKey() {
        String key = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded());


        return "-----BEGIN PUBLIC KEY-----\n" + key + "\n-----END PUBLIC KEY-----";
    }

    /**
     * Generate a new base 64 encoded JWT with the provided {@code principalName} as {@code upn},
     * {@code userId} as {@code sub} and {@code groups} as {@code groups}.
     *
     * @param principalName the user principal name
     * @param userId        the user id
     * @param groups        A set containing the group names the user is a member of
     * @return the base 64 encoded JWT sting containing the provided values.
     */
    public String generateNewJwtToken(String principalName, long userId, Set<String> groups) {
        try {

            Instant now            = Instant.now();
            Instant expirationTime = now.plus(1, ChronoUnit.DAYS);
            JwtBuilder jb = Jwts.builder().setHeaderParam("typ", "JWT")               // type
                                .setHeaderParam("alg", "RS256")             // algorithm
                                .setHeaderParam("kid", "abc-1234567890")    // key id
                                .setSubject(String.valueOf(userId))
                                .setId(UUID.randomUUID().toString())                    // id
                                .claim("iss", issuer).setIssuedAt(Date.from(now))
                                .setExpiration(Date.from(expirationTime))
                                .claim("upn", principalName)                               // user principal name
                                .claim("groups", groups).signWith(this.getRSAPrivate());
            return jb.compact();
        } catch (InvalidKeyException ignore) {
        }
        return null;
    }

    /**
     * Reads the {@link KeyPair} from the file path defined in {@link KeyService#keyPairSaveFile}.
     *
     * @return the {@code KeyPair} if success {@code null} if not.
     */
    private KeyPair readKeyPair() {
        KeyPair result = null;

        try {
            result = deserializeKeyPair(new File(this.keyPairSaveFile));
        } catch (IOException | ClassNotFoundException ex) {
            log.log(Level.SEVERE, "Failed to read keyfile", ex);
        }
        return result;
    }

    /**
     * Saves the {@link KeyPair} to the file path defined in {@link KeyService#keyPairSaveFile}.
     * If the filepath's parent dirs does not exist they wil be generated.
     *
     * @param keyPair the keypair to save
     */
    private void writeKeyPair(KeyPair keyPair) {
        try {
            File saveFile = new File(keyPairSaveFile);
            Optional.ofNullable(saveFile.getParentFile()).ifPresent(File::mkdirs);
            this.serializeKeyPairToFile(keyPair, keyPairSaveFile);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Error writing keypair to file", ex);
        }
    }

    /**
     * Generate the rsa {@link KeyPair}.
     *
     * @return the generated {@link KeyPair}
     */
    private KeyPair createKeyPair() {
        return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    /**
     * Tries to deserialize the {@link KeyPair} from the provided {@link File}.
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
     * Tries to serialize the {@link KeyPair} to the provided {@link File}.
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