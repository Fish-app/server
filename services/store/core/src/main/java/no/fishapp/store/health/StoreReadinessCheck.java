package no.fishapp.store.health;


import no.fishapp.util.restClient.auth.RestClientAuthHandler;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Readiness
@ApplicationScoped
public class StoreReadinessCheck implements HealthCheck {

    private static final String readinessCheck = "Store Service Readiness Check";

    @Inject
    RestClientAuthHandler restClientAuthHandler;

    public HealthCheckResponse call() {
        if (isSystemServiceReachable()) {
            return HealthCheckResponse.up(readinessCheck);
        } else {
            return HealthCheckResponse.down(readinessCheck);
        }
    }

    private boolean isSystemServiceReachable() {
        return requestOk() && restClientOk();
    }

    private boolean restClientOk() {
        return restClientAuthHandler.isReady();
    }

    private boolean requestOk() {
        try {
            Client client = ClientBuilder.newClient();
            client.target("http://localhost:9080/api/store/commodity/all").request().get();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
