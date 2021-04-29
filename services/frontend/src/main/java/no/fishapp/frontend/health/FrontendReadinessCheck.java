package no.fishapp.frontend.health;


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
public class FrontendReadinessCheck implements HealthCheck {
    private static final String readinessCheck = "Frontend Service Readiness Check";

    public HealthCheckResponse call() {
        if (isSystemServiceReachable()) {
            return HealthCheckResponse.up(readinessCheck);
        } else {
            return HealthCheckResponse.down(readinessCheck);
        }
    }


    private boolean isSystemServiceReachable() {
        return requestOk();
    }


    private boolean requestOk() {
        try {
            Client client = ClientBuilder.newClient();
            client.target("http://localhost:80/api/frontend/auth/")
                  .request()
                  .get();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
