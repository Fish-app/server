package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

@Data
public class NewSubscription {

    private DibsOrder order;
    private Checkout checkout;
    private SubscriptionInfo subscription;
    private Notifications notifications;
}
