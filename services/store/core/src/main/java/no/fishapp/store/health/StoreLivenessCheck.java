package no.fishapp.store.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;


@Liveness
@ApplicationScoped
public class StoreLivenessCheck implements HealthCheck {
    MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    long memUsed = memBean.getHeapMemoryUsage().getUsed();
    long memMax = memBean.getHeapMemoryUsage().getMax();


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Store Service Liveness Check")
                                  .withData("memory used", memUsed)
                                  .withData("memory max", memMax)
                                  .status(memUsed < memMax * 0.9).build();
    }
}
