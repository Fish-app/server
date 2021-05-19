package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

import java.util.List;


@Data
public class BulkSubscriptionCharge {

    /**
     * A uniqe string provided to identify the bulk charge
     */
    private String externalBulkChargeId;


    /**
     * List of the subscriptions to renew
     */
    private List<BulkSubscription> subscriptions;
}
