package no.fishapp.user.health;

import no.fishapp.user.boundary.SellerResource;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;


@Liveness
@ApplicationScoped
public class UserLivenessCheck implements HealthCheck {
    MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    long memUsed = memBean.getHeapMemoryUsage().getUsed();
    long memMax = memBean.getHeapMemoryUsage().getMax();

    /**
     * Invokes the health check procedure provided by the implementation of this interface.
     *
     * @return {@link HealthCheckResponse} object containing information about the health check result
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("User Service Liveness Check")
                                  .withData("memory used", memUsed)
                                  .withData("memory max", memMax)
                                  .status(memUsed < memMax * 0.9).build();
    }
}
