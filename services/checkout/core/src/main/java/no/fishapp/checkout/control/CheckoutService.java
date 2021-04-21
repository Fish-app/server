package no.fishapp.checkout.control;

import no.fishapp.checkout.model.DTO.NewSubscription;
import no.fishapp.checkout.model.dibsapi.Checkout;
import no.fishapp.checkout.model.dibsapi.Item;
import no.fishapp.checkout.model.dibsapi.Order;
import no.fishapp.checkout.model.dibsapi.SubscriptionInfo;
import no.fishapp.checkout.model.enums.Currencies;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class CheckoutService {

    @Inject
    @ConfigProperty(name = "fishapp.checkout.items.subscription.name")
    private String subscriptionItemName;

    @Inject
    @ConfigProperty(name = "fishapp.checkout.return.url")
    private String returnUrl;

    @Inject
    @ConfigProperty(name = "fishapp.checkout.tos.url")
    private String tosUrl;


    @PersistenceContext
    EntityManager entityManager;


    public Optional<Item> getItem(String itemId){
        return Optional.ofNullable(entityManager.find(Item.class, itemId));
    }

    public Item getSubscriptionItem(){
        // ehhh to litte time
        return this.getItem(subscriptionItemName).orElse(null);
    }

    public Checkout getDefaultCheckout(){
        Checkout checkout = new Checkout();

        checkout.setIntegrationType("hostedPaymentPage");
        checkout.setReturnUrl(returnUrl);
        checkout.setTermsUrl(tosUrl);
        checkout.setCharge(true);
        return checkout;
    }

    public SubscriptionInfo getDefaultSubInfo(){
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();

        // the end date is required so...
        subscriptionInfo.setEndDate(Instant.now().plus(7, ChronoUnit.YEARS).toString());
        subscriptionInfo.setInterval(3);
        return subscriptionInfo;
    }

    public void chargeSubscriptions(){

    }





    public NewSubscription createNewSubscription(){
        Item item = this.getSubscriptionItem();


        // order
        Order order = new Order();

        order.setItems(List.of(item));
        order.setAmount(1);
        order.setCurrency(Currencies.NOK);
        order.setReference("jdkjfk");

        // chekout
        Checkout checkout = this.getDefaultCheckout();

        // sub info

        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();

        subscriptionInfo.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS).toString());
        subscriptionInfo.setInterval(0);

        // new sub req

        NewSubscription newSubscription = new NewSubscription();

        newSubscription.setOrder(order);
        newSubscription.setCheckout(checkout);
        newSubscription.setSubscription(subscriptionInfo);

        return newSubscription;

    }
}
