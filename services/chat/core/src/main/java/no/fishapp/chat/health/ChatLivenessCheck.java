package no.fishapp.chat.health;

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
public class ChatLivenessCheck implements HealthCheck {

    MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    long memUsed = memBean.getHeapMemoryUsage().getUsed();
    long memMax = memBean.getHeapMemoryUsage().getMax();


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Chat Service Liveness Check").withData("memory used", memUsed)
                                  .withData("memory max", memMax).status(memUsed < memMax * 0.9).build();
    }
}
