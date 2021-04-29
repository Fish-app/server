package no.fishapp.user.health;


import no.fishapp.user.boundary.SellerResource;
import no.fishapp.util.restClient.auth.RestClientAuthHandler;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Readiness
@ApplicationScoped
public class UserReadinessCheck implements HealthCheck {
    private static final String readinessCheck = "User Service Readiness Check";

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
            client.target("http://localhost:80/api/user/admin/ready")
                  .request()
                  .get();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
