package no.fishapp.checkout.control;

import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import no.fishapp.checkout.client.DibsPaymentClient;
import no.fishapp.checkout.exeptions.UserAlreadySubscribedException;
import no.fishapp.checkout.exeptions.WebHookException;
import no.fishapp.checkout.model.SubscribedUser;
import no.fishapp.checkout.model.dibsapi.*;
import no.fishapp.checkout.model.dibsapi.responses.BulkChargeResponse;
import no.fishapp.checkout.model.dibsapi.responses.SubscriptionResponse;
import no.fishapp.checkout.model.enums.Currencies;
import no.fishapp.checkout.model.enums.SubscriptionStatus;
import no.fishapp.checkout.model.notGreatSolutions.SimpleDbUpdatePollTicket;
import no.fishapp.util.exceptionmappers.NoJwtTokenException;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
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

    @Inject
    WebHookBuilder webHookBuilder;

    @Inject
    PaymentUpdaterPoller paymentUpdaterPoller;

    public Optional<Item> getItem(String itemId) {
        return Optional.ofNullable(entityManager.find(Item.class, itemId));
    }


    public static String GET_ALL_SUBSCRIBED_USERS;

    public static String GET_ORDER_FROM_ID = "SELECT do FROM DibsOrder do where do.id = :oid";
    public static String GET_USER_SUBSCRIPTION = "SELECT su FROM SubscribedUser su WHERE su.userid = :uid";

    @Asynchronous
    public void chargeSubscriptions() {
        try {
            SimpleDbUpdatePollTicket ticket        = paymentUpdaterPoller.pollForUpdater().get();
            var                      updaterStatus = ticket.getUpdaterStatus();

            if (updaterStatus == SimpleDbUpdatePollTicket.UpdaterStatus.ignore) {
                return;
            } else if (updaterStatus == SimpleDbUpdatePollTicket.UpdaterStatus.Validator) {
                // TODO: validate the updater has done his job
                //       easyest wold be registering a callback to run when the chosen is removed from the db
            } else if (updaterStatus == SimpleDbUpdatePollTicket.UpdaterStatus.Chosen) {
                List<SubscribedUser> commodities = entityManager
                        .createQuery(GET_ALL_SUBSCRIBED_USERS, SubscribedUser.class)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();

                /*
                turns out Entity manager does not have a batch insert command so...
                this is not a great way of doing this and should probably make the
                changes to the list an update the tables manually
                but, you know, laziness.
                 */

                Map<String, SubscribedUser> toRenew      = new ConcurrentHashMap<>();
                List<BulkSubscription>      renewOrders  = Collections.synchronizedList(new ArrayList<>());
                SubscriptionOrderBuilder    orderBuilder = new SubscriptionOrderBuilder(this.getSubscriptionItem());


                commodities.stream().parallel().forEach(subscribedUser -> {
                    if (subscribedUser.isWillRenew()) {
                        toRenew.put(subscribedUser.getSubscriptionId(), subscribedUser);

                        DibsOrder newOrder = orderBuilder.generateOrder(subscribedUser.getUserid());
                        newOrder.setOrderOwner(subscribedUser);
                        BulkSubscription bulkSubscription = new BulkSubscription();
                        bulkSubscription.setSubscriptionId(subscribedUser.getSubscriptionId());
                        bulkSubscription.setDibsOrder(newOrder);

                        subscribedUser.addOrder(newOrder);
                        renewOrders.add(bulkSubscription);
                    } else {
                        subscribedUser.setSubscriptionStatus(SubscriptionStatus.NOT_ACTIVE);
                        entityManager.persist(subscribedUser);
                    }
                });


                BulkSubscriptionCharge bulkSubscriptionCharge = new BulkSubscriptionCharge();

                String chargeId = UUID.randomUUID().toString();

                bulkSubscriptionCharge.setExternalBulkChargeId(chargeId);
                bulkSubscriptionCharge.setSubscriptions(renewOrders);

                Map<String, String> resp = this.dibsPaymentClient
                        .makeBulkCharge(this.dibsApiKey, bulkSubscriptionCharge);
                String bulkid = resp.get("bulkId");

                BulkChargeResponse bulkChargeResponse = this.dibsPaymentClient
                        .getBulkOrderDetails(bulkid, this.dibsApiKey, renewOrders.size(), 0);

                bulkChargeResponse.getPage().stream().parallel().forEach(bulkChargeResponseItem -> {
                    String         subId = bulkChargeResponseItem.getSubscriptionId();
                    SubscribedUser user  = toRenew.get(subId);
                    String         payId = bulkChargeResponseItem.getPaymentId();

                    // yehh not gr8 m8
                    DibsOrder dibsOrder = user.getSubscriptionDibsOrders()
                                              .get(user.getSubscriptionDibsOrders().size() - 1);

                    switch (bulkChargeResponseItem.getStatus()) {
                        case "Succeeded":


                            user.setSubscriptionStatus(SubscriptionStatus.OK);
                            dibsOrder.setId(payId);
                            dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentChargeSuccess);
                            entityManager.persist(dibsOrder);
                            entityManager.persist(user);

                            break;
                        case "Failed":
                            user.setSubscriptionStatus(SubscriptionStatus.PAY_ERROR);
                            dibsOrder.setId(payId);
                            dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentChargeFailed);

                            user.setWillRenew(false);

                            entityManager.persist(dibsOrder);
                            entityManager.persist(user);
                            break;
                        default:
                            log.severe("Unexpected subscription response");
                            user.setSubscriptionStatus(SubscriptionStatus.PAY_ERROR);
                            dibsOrder.setId(payId);
                            dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentChargeFailed);

                            user.setWillRenew(false);

                            entityManager.persist(dibsOrder);
                            entityManager.persist(user);
                    }
                });


            }

            paymentUpdaterPoller.IsDone(ticket);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RestClientHttpException e) {
            e.printStackTrace();
        }


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

    private Optional<DibsOrder> getDibsOrder(String orderId) {
        TypedQuery<DibsOrder> query = entityManager.createQuery(GET_ORDER_FROM_ID, DibsOrder.class);
        query.setParameter("oid", orderId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    public void chargeSuccessWebhook(String orderId) {
        Optional<DibsOrder> order     = getDibsOrder(orderId);
        DibsOrder           dibsOrder = order.orElseThrow(WebHookException::new);

        dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentChargeSuccess);
        SubscribedUser subscribedUser = dibsOrder.getOrderOwner();

        subscribedUser.setSubscriptionStatus(SubscriptionStatus.OK);
    }

    @SneakyThrows
    public void chargeFailedWebhook(String orderId) {
        Optional<DibsOrder> order     = getDibsOrder(orderId);
        DibsOrder           dibsOrder = order.orElseThrow(WebHookException::new);

        dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentChargeFailed);
        SubscribedUser subscribedUser = dibsOrder.getOrderOwner();

        subscribedUser.setSubscriptionStatus(SubscriptionStatus.PAY_ERROR);
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

        return subscribedUser.map(user -> user.getSubscriptionStatus() == SubscriptionStatus.OK).orElse(false);
    }

    public SubscriptionStatus getSubscriptionStatus(long userId) {
        return getSubscribedUser(userId).map(SubscribedUser::getSubscriptionStatus)
                                        .orElse(SubscriptionStatus.NOT_ACTIVE);

    }


    public Optional<SubscriptionResponse> newSubscription() throws UserAlreadySubscribedException {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            throw new NoJwtTokenException();
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
        DibsOrder       order           = newSubscription.getOrder();

        try {
            SubscriptionResponse resp = dibsPaymentClient.newSubscription(dibsApiKey, newSubscription);
            order.setId(resp.getPaymentId());
            order.setOrderOwner(subscribedUser);
            entityManager.persist(order);
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
        Notifications    notifications    = this.createNotifications();


        // new sub req

        NewSubscription newSubscription = new NewSubscription();
        newSubscription.setOrder(order);
        newSubscription.setCheckout(checkout);
        newSubscription.setSubscription(subscriptionInfo);

        newSubscription.setNotifications(notifications);

        return newSubscription;

    }


    public Notifications createNotifications() {


        Notifications notifications = new Notifications();
        notifications.setWebHooks(List.of(webHookBuilder.getPaymentCreatedWebhook(),
                                          webHookBuilder.getPaymentReservationCreatedWebhook(),
                                          webHookBuilder.getPaymentChargeCreatedWebhook(),
                                          webHookBuilder.getPaymentChargeFailedWebhook()));
        return notifications;
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
        dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.LinkCreated);
        dibsOrder.setReference(String.format("USER:%s-ITEM:%s-%s", userId, suscriptionItem.getId(), UUID.randomUUID()));
        dibsOrder.calculateAndSetAmount();


        return dibsOrder;
    }

    private static class SubscriptionOrderBuilder {

        private final Item subscriptionItem;

        public SubscriptionOrderBuilder(Item subscriptionItem) {
            this.subscriptionItem = subscriptionItem;
        }

        public DibsOrder generateOrder(long userId) {
            DibsOrder dibsOrder = new DibsOrder();
            dibsOrder.setItems(List.of(subscriptionItem));
            dibsOrder.setAmount(1);
            dibsOrder.setCurrency(Currencies.NOK);
            dibsOrder.setDibsPaymentStatus(DibsPaymentStatus.PaymentCreated);
            dibsOrder.setReference(String.format("BULK-C-USER:%s-ITEM:%s-%s",
                                                 userId,
                                                 subscriptionItem.getId(),
                                                 UUID.randomUUID()));
            dibsOrder.calculateAndSetAmount();
            return dibsOrder;
        }
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
