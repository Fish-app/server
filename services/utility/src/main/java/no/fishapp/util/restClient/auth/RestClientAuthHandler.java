package no.fishapp.util.restClient.auth;


import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
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

    public static RestClientAuthHandler getInstance(){
        if (instance == null){
            //todo: this is a issue
            instance = new RestClientAuthHandler();
            instance.init();
        }
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

    private PublicKey jwtPubKey;

    @PostConstruct
    @Asynchronous
    public void init(){
        log.log(Level.WARNING,"created rest auth handler");
        instance = this;
        this.jwtPubKey = getTokenPubKey();
        this.jwtParser = buildJwtParser();

        refreshToken();


    }

    @SneakyThrows
    private PublicKey getTokenPubKey(){
        // todo: handle errors
        var pkey = authClient.getPubKey();
        String publicKeyPEM = pkey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);

    }

    private JwtParser buildJwtParser(){
        return Jwts.parserBuilder().setSigningKey(this.jwtPubKey).requireIssuer(fishappJwtIssuer).build();
    }


    private String tokenString;




    public String getAuthTokenHeader() {
        return tokenString;
    }


    private void refreshToken(){
        //todo: if failed wait 30 sec and try again elns

        Response response = authClient.login(new UsernamePasswordData(password, username));





        String authHeader = (String) response.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            System.out.println(authHeader);
            String token = authHeader.replaceFirst("Bearer ", "");
            Instant refreshTime;
            try {
                this.tokenString = authHeader;
                log.log(Level.ALL, "Refreshed jwt token");
                var jwtClaims = jwtParser.parseClaimsJws(token);
                refreshTime = jwtClaims.getBody().getExpiration().toInstant().minus(20, ChronoUnit.MINUTES);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error geting inter container login token. Retrying in 10s");
                refreshTime = Instant.now().plus(10, ChronoUnit.SECONDS);
            }
           Duration waitTime = Duration.between(Instant.now(),refreshTime);
            //TODO: handle if negative (or chek that the jwt parser wil cath it)
            scheduledExec.schedule(this::refreshToken, waitTime.getSeconds(), TimeUnit.SECONDS);
        }

    }

}
