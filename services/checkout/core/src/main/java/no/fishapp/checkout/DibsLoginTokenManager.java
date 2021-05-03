package no.fishapp.checkout;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.extern.java.Log;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.checkout.client.AuthClient;
import no.fishapp.util.exceptionmappers.RestClientHttpException;

import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * currently not used because dibs do not support auth header bigger than 32 chars. if they end up doing so
 * this cold be used to issue jwt keys for the webhooks to use.
 * just replace the {@code @PermitAll} with a {@code @RolesAllowed(Group.API_CALLBACK_GROUP_NAME)} and
 * use the key provided by the {@link DibsLoginTokenManager#getJwtToken()} method in the {@link no.fishapp.checkout.control.WebHookBuilder} to set the key.
 */
@Log
//@Singleton
//@Startup
public class DibsLoginTokenManager {

    /**
     * The dibs api webhook callback username
     */
    //@Inject
    //@ConfigProperty(name = "fishapp.service.dibsapi.username", defaultValue = "webhook_default")
    private String dibsApiUserUsername;

    /**
     * The dibs api webhook callback password
     */
    //@Inject
    //@ConfigProperty(name = "fishapp.service.dibsapi.password", defaultValue = "fishapp")
    private String dibsApiUserPassword;


    //@Inject
    //@RestClient
    AuthClient authClient;

    //@Resource
    ManagedScheduledExecutorService scheduledExec;

    @Getter
    private Optional<String> jwtToken = Optional.empty();


    //    @PostConstruct
    //    @Asynchronous
    public void getToken() {
        log.info("starting dibs login token refresh");
        Instant nextRefresh;
        try {
            UsernamePasswordData usernamePasswordData = new UsernamePasswordData();
            usernamePasswordData.setPassword(dibsApiUserPassword);
            usernamePasswordData.setUserName(dibsApiUserUsername);

            Response response           = authClient.getDibsLoginToken(usernamePasswordData);
            String   jwtTokenAuthString = (String) response.getHeaders().getFirst("Authorization");
            String   token              = jwtTokenAuthString.replaceFirst("Bearer ", "");

            // todo: ehhhehehhehe
            int    sigCutoff        = token.lastIndexOf('.');
            String withoutSignature = token.substring(0, sigCutoff + 1);

            Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);

            nextRefresh = untrusted.getBody().getExpiration().toInstant().minus(20, ChronoUnit.MINUTES);
            this.jwtToken = Optional.of(jwtTokenAuthString);
            log.info("successfully refreshed dibs callback token");
        } catch (RestClientHttpException e) {
            log.warning("error fetching dibs login token retying in 5 sec");
            nextRefresh = Instant.now().plus(5, ChronoUnit.SECONDS);
        }

        Duration waitTime = Duration.between(Instant.now(), nextRefresh);

        scheduledExec.schedule(this::getToken, waitTime.getSeconds(), TimeUnit.SECONDS);
    }

}
