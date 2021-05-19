package no.fishapp.media.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;


/**
 * Endpoints used by Kubernetes Health Checking
 */
@Liveness
@ApplicationScoped
public class MediaLivenessCheck implements HealthCheck {
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
        return HealthCheckResponse.named("Media service Liveness Check")
                                  .withData("memory used", memUsed)
                                  .withData("memory max", memMax)
                                  .status(memUsed < memMax * 0.9).build();
    }
}
