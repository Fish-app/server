package no.fishapp.util.restClient;


import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.java.Log;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * The {@link RestClientAuthHandler} is responsibel for providing the {@link AuthBaseClientInterface} with valid,
 * up to date jwt tokens. It is also responsibele for refreshing them before expiry
 */
@Log
@Singleton
@Startup
public class RestClientAuthHandler {

    // singelton pattern
    private static RestClientAuthHandler instance;

    public static RestClientAuthHandler getInstance() {
        return instance;
    }

    /**
     * the username for container login
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "fishapp")
    private String username;


    /**
     * the password for container login
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;

    /**
     * the jwt issuer to verify
     */
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "fishapp")
    private String fishappJwtIssuer;

    /**
     * The auth client used to communicate with the auth service
     */
    @Inject
    @RestClient
    ContainerAuthClient authClient;

    @Resource
    ManagedScheduledExecutorService scheduledExec;

    private JwtParser jwtParser;

    private String tokenString;


    private PublicKey jwtPubKey;
    private boolean isKeyValid = false;


    /**
     * Used for health check's to verify the the token is fetched and the services {@code @RestClient} are ready.
     *
     * @return whether or not the auth handler has ready tokens.
     */
    public boolean isReady() {
        return isKeyValid && tokenString != null;
    }

    /**
     * Sets the instance and starts the token loop asynchronously.
     */
    @PostConstruct
    public void startup() {
        instance = this;

        tokenLoop();
    }

    /**
     * Returns the ready authentication string to put in the rest clients auth header.
     *
     * @return the auth header string.
     */
    public String getAuthTokenHeader() {
        return tokenString;
    }


    /**
     * The token loop responsible for maintaining valid tokens for the modules clients to use
     */
    @Asynchronous
    private void tokenLoop() {
        Instant refreshTime;
        log.finer("starting token refresh");

        try {
            if (! isKeyValid) {
                log.finer("fetching signing key");
                this.jwtPubKey = getTokenPubKey();
                this.jwtParser = buildJwtParser();
                log.finer("successfully refreshed container signing key");
            }

            String authHeader = getLoginToken();
            this.tokenString = authHeader;

            refreshTime = this.getRefreshTime(authHeader);

            log.info("successfully refreshed container token");
        } catch (RestClientHttpException e) {
            log.log(Level.WARNING,
                    String.format("Conection http %s error geting inter container login token. Retrying in 10s",
                                  e.getResponse().getStatus()
                    )
            );
            refreshTime = Instant.now().plus(10, ChronoUnit.SECONDS);
        } catch (SignatureException e) {
            log.log(Level.WARNING, "Error validating inter container login token. refreshing pub key");
            isKeyValid  = false;
            refreshTime = Instant.now().plus(1, ChronoUnit.SECONDS);
        } catch (ConnectException e) {
            log.log(Level.WARNING, "Error fetching key. retying in 5 sec");
            refreshTime = Instant.now().plus(5, ChronoUnit.SECONDS);
        }

        Duration waitTime = Duration.between(Instant.now(), refreshTime);

        //TODO: handle if negative (or chek that the jwt parser wil cath it)
        scheduledExec.schedule(this::tokenLoop, waitTime.getSeconds(), TimeUnit.SECONDS);
    }


    /**
     * Fetches the token RSA pub key and returns it as a {@link PublicKey}
     *
     * @return the token RSA {@code PublicKey}
     * @throws RestClientHttpException if a non 200 http code is thrown
     * @throws ConnectException        if there is an error connecting
     */
    private PublicKey getTokenPubKey() throws RestClientHttpException, ConnectException {
        // todo: handle errors

        String pkey = authClient.getPubKey();
        String publicKeyPEM = pkey.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "")
                                  .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes());

        try {
            KeyFactory         keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec    = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Fetches the login token from the auth module
     *
     * @return the login token.
     * @throws RestClientHttpException if a non 200 http-code is returned
     */
    public String getLoginToken() throws RestClientHttpException {
        Response response = authClient.login(new UsernamePasswordData(password, username));
        return (String) response.getHeaders().getFirst("Authorization");
    }

    /**
     * Builds the {@link JwtParser} to parse the token to get the expiry time. The builder uses the {@link RestClientAuthHandler#jwtPubKey}
     * to ensure the token is valid
     *
     * @return the {@code JwtParser} for the current pub key
     */
    private JwtParser buildJwtParser() {
        return Jwts.parserBuilder().setSigningKey(this.jwtPubKey).requireIssuer(fishappJwtIssuer).build();
    }

    /**
     * returns the token refresh time i.e. 20 min before the token expires
     *
     * @param authHeader the auth header to extract the token data and subsequent time data from
     *
     * @return the {@link Instant} the token shold be refreshed
     * @throws SignatureException if the token in the header has an invalid signature
     */
    private Instant getRefreshTime(String authHeader) throws SignatureException {
        String token = authHeader.replaceFirst("Bearer ", "");

        log.log(Level.ALL, "Refreshed jwt token");
        var jwtClaims = jwtParser.parseClaimsJws(token);
        isKeyValid = true;
        return jwtClaims.getBody().getExpiration().toInstant().minus(20, ChronoUnit.MINUTES);


    }

}
