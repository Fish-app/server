package no.fishapp.checkout.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.checkout.client.DibsPaymentClient;
import no.fishapp.checkout.client.exceptionHandlers.RestClientHttpException;
import no.fishapp.checkout.model.DTO.NewSubscription;
import no.fishapp.checkout.model.DTO.SubscriptionResponse;
import no.fishapp.checkout.model.dibsapi.Checkout;
import no.fishapp.checkout.model.dibsapi.Item;
import no.fishapp.checkout.model.dibsapi.Order;
import no.fishapp.checkout.model.dibsapi.SubscriptionInfo;
import no.fishapp.checkout.model.enums.Currencies;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Transactional
@RequestScoped
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


    @Inject
    @ConfigProperty(name = "fishapp.checkout.dibs.api.key")
    private String dibsApiKey;


    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;


    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @RestClient
    DibsPaymentClient dibsPaymentClient;

    public Optional<Item> getItem(String itemId){
        return Optional.ofNullable(entityManager.find(Item.class, itemId));
    }



    public void chargeSubscriptions(){

    }



    public Optional<SubscriptionResponse> newSubscription(){
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }
        long currentUserId = Long.parseLong(jwtSubject.get().get());


        NewSubscription      newSubscription = this.createNewSubscription(currentUserId);
        try {
            SubscriptionResponse resp = dibsPaymentClient.newSubscription(dibsApiKey, newSubscription);
            
            return Optional.of(resp);
        } catch (RestClientHttpException e) {
            var a = e.getResponse();
            System.out.println("STATUSCODE: " + a.getStatus());
            System.out.println("CONTENT: " + a.readEntity(String.class));

            e.printStackTrace();
            return Optional.empty();
        }


    }


    public NewSubscription createNewSubscription(long userId){


        Order order =  this.getDefaultSubscriptionOrder(userId);
        Checkout checkout = this.getDefaultCheckout();
        SubscriptionInfo subscriptionInfo = this.getDefaultSubInfo();

        // new sub req

        NewSubscription newSubscription = new NewSubscription();
        newSubscription.setOrder(order);
        newSubscription.setCheckout(checkout);
        newSubscription.setSubscription(subscriptionInfo);

        return newSubscription;

    }


    /*
    I know this is not ideal. Hardcoding implementations like this makes change harder
    there are multiple alternatives, the best being simply generating from values in the database.

    1. it allows normal humans to change stuff the through an interface.
    2. you do not have to change code when implementing new stuff
     */

    public Item getSubscriptionItem(){
        // ehhh to litte time
        return this.getItem(subscriptionItemName).orElse(null);
    }

    public Order getDefaultSubscriptionOrder(long userId){
        Item suscriptionItem = this.getSubscriptionItem();

        Order order = new Order();

        order.setItems(List.of(suscriptionItem));
        order.setAmount(1);
        order.setCurrency(Currencies.NOK);
        order.setReference(String.format("USER:%s-ITEM:%s", userId, suscriptionItem.getId()));

        return order;
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
        subscriptionInfo.setEndDate(Instant.now().plus(100000, ChronoUnit.DAYS).toString());
        subscriptionInfo.setInterval(3);
        return subscriptionInfo;
    }
}
