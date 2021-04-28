package no.fishapp.checkout.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.checkout.client.DibsPaymentClient;
import no.fishapp.checkout.client.exceptionHandlers.RestClientHttpException;
import no.fishapp.checkout.exeptions.UserAlreadySubscribedException;
import no.fishapp.checkout.model.SubscribedUser;
import no.fishapp.checkout.model.dibsapi.*;
import no.fishapp.checkout.model.dibsapi.responses.SubscriptionResponse;
import no.fishapp.checkout.model.enums.Currencies;
import no.fishapp.checkout.model.enums.SubscriptionStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    public Optional<Item> getItem(String itemId) {
        return Optional.ofNullable(entityManager.find(Item.class, itemId));
    }


    public static String GET_ALL_ACTIVE_SUBSCRIPTIONS;
    public static String GET_USER_SUBSCRIPTION = "SELECT su FROM SubscribedUser su WHERE su.userid = :uid";


    public void chargeSubscriptions() {


    }

    private Optional<SubscribedUser> getSubscribedUser(long userId) {
        TypedQuery<SubscribedUser> query = entityManager.createQuery(GET_USER_SUBSCRIPTION, SubscribedUser.class);
        query.setParameter("uid", userId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    public boolean cansleSubscription() {
        if (jwtSubject.get().isEmpty()) {
            // this is bad but unlikely
            log.log(Level.SEVERE, "Error reading jwt token");
            return false;
        }
        long currentUserId = Long.parseLong(jwtSubject.get().get());


        SubscribedUser subscribedUser = entityManager.find(SubscribedUser.class, currentUserId);

        if (subscribedUser != null) {
            subscribedUser.setWillRenew(false);
            // this is to enshure the user wil not be charged again even if the
            // call to cansle the payment objet fails
            entityManager.persist(subscribedUser);
            try {
                var resp = dibsPaymentClient.cancelPayment(subscribedUser.getSubscriptionId());

                subscribedUser.setSubscriptionId(null);
                entityManager.persist(subscribedUser);
                return true;
            } catch (RestClientHttpException e) {
                var a = e.getResponse();
                System.out.println("STATUSCODE: " + a.getStatus());
                System.out.println("CONTENT: " + a.readEntity(String.class));

                return false;
            }


        } else {
            return false;
        }


    }

    public boolean isSubscriptionValid(long userId) {
        Optional<SubscribedUser> subscribedUser = getSubscribedUser(userId);

        return subscribedUser.map(user -> {
            switch (user.getSubscriptionStatus()) {
                case OK:
                    return true;
                case PENDING:
                    try {
                        dibsPaymentClient.getSubscriptionDetails(user.getSubscriptionId(), dibsApiKey);
                        user.setSubscriptionStatus(SubscriptionStatus.OK);
                        entityManager.persist(user);
                        return true;
                    } catch (RestClientHttpException e) {
                        var a = e.getResponse();
                        System.out.println("STATUSCODE: " + a.getStatus());
                        System.out.println("CONTENT: " + a.readEntity(String.class));
                        return false;
                    }
                case NOT_ACTIVE:
                default:
                    return false;
            }
        }).orElse(false);
    }


    public Optional<SubscriptionResponse> newSubscription() throws UserAlreadySubscribedException {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }
        long currentUserId = Long.parseLong(jwtSubject.get().get());

        Optional<SubscribedUser> subscribedUserOptional = getSubscribedUser(currentUserId);

        SubscribedUser subscribedUser = subscribedUserOptional.orElseGet(() -> {
            var newUser = new SubscribedUser();
            newUser.setUserid(currentUserId);
            newUser.setWillRenew(true);
            return newUser;
        });

        if (subscribedUser.getSubscriptionStatus() == SubscriptionStatus.OK) {
            throw new UserAlreadySubscribedException();
        }

        subscribedUser.setSubscriptionStatus(SubscriptionStatus.PENDING);


        NewSubscription newSubscription = this.createNewSubscription(currentUserId);
        entityManager.persist(newSubscription.getOrder());
        try {
            SubscriptionResponse resp = dibsPaymentClient.newSubscription(dibsApiKey, newSubscription);
            subscribedUser.setSubscriptionId(resp.getPaymentId());
            subscribedUser.addOrder(newSubscription.getOrder());


            return Optional.of(resp);
        } catch (RestClientHttpException e) {
            var a = e.getResponse();
            System.out.println("STATUSCODE: " + a.getStatus());
            System.out.println("CONTENT: " + a.readEntity(String.class));

            e.printStackTrace();
            return Optional.empty();
        } finally {
            entityManager.persist(subscribedUser);
        }


    }


    public NewSubscription createNewSubscription(long userId) {


        DibsOrder        order            = this.getDefaultSubscriptionOrder(userId);
        Checkout         checkout         = this.getDefaultCheckout();
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

    public Item getSubscriptionItem() {
        // ehhh to litte time
        return this.getItem(subscriptionItemName).orElse(null);
    }

    public DibsOrder getDefaultSubscriptionOrder(long userId) {
        Item suscriptionItem = this.getSubscriptionItem();

        DibsOrder dibsOrder = new DibsOrder();

        dibsOrder.setItems(List.of(suscriptionItem));
        dibsOrder.setAmount(1);
        dibsOrder.setCurrency(Currencies.NOK);
        dibsOrder.setReference(String.format("USER:%s-ITEM:%s-%s", userId, suscriptionItem.getId(), UUID.randomUUID()));
        dibsOrder.calculateAndSetAmount();

        return dibsOrder;
    }

    public Checkout getDefaultCheckout() {
        Checkout checkout = new Checkout();

        checkout.setIntegrationType("hostedPaymentPage");
        checkout.setReturnUrl(returnUrl);
        checkout.setTermsUrl(tosUrl);
        checkout.setCharge(true);
        return checkout;
    }

    public SubscriptionInfo getDefaultSubInfo() {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();

        // the end date is required so...
        subscriptionInfo.setEndDate(Instant.now().plus(1000, ChronoUnit.DAYS).toString());
        subscriptionInfo.setInterval(3);
        return subscriptionInfo;
    }
}
