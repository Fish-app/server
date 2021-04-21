package no.fishapp.checkout.model.DTO;

import lombok.Data;
import no.fishapp.checkout.model.dibsapi.Checkout;
import no.fishapp.checkout.model.dibsapi.Order;
import no.fishapp.checkout.model.dibsapi.SubscriptionInfo;

@Data
public class NewSubscription {
    private Order order;
    private Checkout checkout;
    private SubscriptionInfo subscription;
}
