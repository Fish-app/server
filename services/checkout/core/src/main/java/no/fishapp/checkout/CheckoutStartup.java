package no.fishapp.checkout;



import lombok.SneakyThrows;
import no.fishapp.checkout.control.CheckoutService;
import no.fishapp.checkout.model.dibsapi.Item;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Startup actions for authentication that are executed on server start.
 */
@Singleton
@Startup
public class CheckoutStartup {

@Inject
private CheckoutService checkoutService;

    @PersistenceContext
    EntityManager entityManager;


    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "fishapp")
    private String username;

    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;

    @Inject
    @ConfigProperty(name = "fishapp.checkout.items.subscription.name")
    private String subscriptionItemName;

    @PostConstruct
    @Asynchronous
    public void initialize() {
        this.createSubscriptionItem();

    }


    public void createSubscriptionItem() {
        // this entire method is not great and should be changed

        var itemOptional = checkoutService.getItem(subscriptionItemName);

        if (itemOptional.isEmpty()) {

            // item
            Item item = new Item();
            item.setReference("Subscription item");
            item.setName("seller subscription");
            item.setQuantity(1);
            item.setUnit("unit");
            item.setUnitPrice(1);
            item.setTaxRate(0);
            item.setTaxAmount(0);
            item.setGrossTotalAmount(1);
            item.setNetTotalAmount(1);


            entityManager.persist(item);
        }
    }

    @SneakyThrows
    @Schedules({
            @Schedule(dayOfMonth="First", hour = "3"),
    })
    public void chargeSubscriptionTask(){

        // this may seem stupid mainly because it technically is but.
        // if this task is triggered in a cluster of these containers without this spreading the api
        // may respond unpredictably this is not ideal an got to be handled.
        // A better solution wold be having a message broker like kafka where all the auth
        // containers broadcasts a random number and the service with the highest number
        // takes responsibility and handles the refreshing.
        //
        // But time is fleeting and life is short so this shitty wait solution got to be good enough
        Random random = new Random();
        int sleeptime = random.nextInt(3000);
        Thread.sleep(sleeptime);

        checkoutService.chargeSubscriptions();

    }
}