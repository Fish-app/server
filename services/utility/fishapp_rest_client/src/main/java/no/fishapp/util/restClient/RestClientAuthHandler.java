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
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
@Singleton
@Startup
public class RestClientAuthHandler {

    private static RestClientAuthHandler instance;

    public static RestClientAuthHandler getInstance() {
        return instance;
    }


    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "fishapp")
    private String username;


    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "fishapp")
    private String fishappJwtIssuer;


    @Inject
    @RestClient
    ContainerAuthClient authClient;

    @Resource
    ManagedScheduledExecutorService scheduledExec;

    private JwtParser jwtParser;

    private String tokenString;


    private PublicKey jwtPubKey;
    private boolean isKeyValid = false;

    public boolean isReady() {
        return isKeyValid && tokenString != null;
    }

    @PostConstruct
    public void startup() {
        instance = this;

        tokenLoop();
    }

    /**
     *
     */
    @Asynchronous
    private void tokenLoop() {
        Instant refreshTime;
        log.finer("starting token refresh");

        try {

            if (!isKeyValid) {
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
                                  e.getResponse().getStatus()));
            refreshTime = Instant.now().plus(10, ChronoUnit.SECONDS);
        } catch (SignatureException e) {
            log.log(Level.WARNING, "Error validating inter container login token. refreshing pub key");
            isKeyValid = false;
            refreshTime = Instant.now().plus(1, ChronoUnit.SECONDS);
        } catch (ConnectException e) {
            log.log(Level.WARNING, "Error fetching key. retying in 5 sec");
            refreshTime = Instant.now().plus(5, ChronoUnit.SECONDS);
        }

        Duration waitTime = Duration.between(Instant.now(), refreshTime);

        //TODO: handle if negative (or chek that the jwt parser wil cath it)
        scheduledExec.schedule(this::tokenLoop, waitTime.getSeconds(), TimeUnit.SECONDS);
    }

    public String getAuthTokenHeader() {
        return tokenString;
    }

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

    public String getLoginToken() throws RestClientHttpException {
        Response response = authClient.login(new UsernamePasswordData(password, username));
        return (String) response.getHeaders().getFirst("Authorization");
    }

    private JwtParser buildJwtParser() {
        return Jwts.parserBuilder().setSigningKey(this.jwtPubKey).requireIssuer(fishappJwtIssuer).build();
    }


    private Instant getRefreshTime(String authHeader) throws SignatureException {
        String token = authHeader.replaceFirst("Bearer ", "");

        log.log(Level.ALL, "Refreshed jwt token");
        var jwtClaims = jwtParser.parseClaimsJws(token);
        isKeyValid = true;
        return jwtClaims.getBody().getExpiration().toInstant().minus(20, ChronoUnit.MINUTES);


    }

}
