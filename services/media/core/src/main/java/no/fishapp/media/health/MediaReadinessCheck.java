package no.fishapp.media.health;


import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Readiness
@ApplicationScoped
public class MediaReadinessCheck implements HealthCheck {
    private static final String readinessCheck = "Media Service Readiness Check";

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
            client.target("http://localhost:80/admin")
                  .request()
                  .get();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
