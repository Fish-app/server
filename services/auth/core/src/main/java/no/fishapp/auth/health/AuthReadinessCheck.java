package no.fishapp.auth.health;


import no.fishapp.auth.boundary.AuthResource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Readiness
@ApplicationScoped
public class AuthReadinessCheck implements HealthCheck {
    private static final String readinessCheck = "Auth Service Readiness Check";


    public HealthCheckResponse call() {
        if (isSystemServiceReachable()) {
            return HealthCheckResponse.up(readinessCheck);
        } else {
            return HealthCheckResponse.down(readinessCheck);
        }
    }

    private boolean isSystemServiceReachable() {
        try {
            Client client = ClientBuilder.newClient();
            client.target("http://localhost:80/api/auth/key.pem")
                  .request()
                  .get();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
