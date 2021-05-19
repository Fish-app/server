package no.fishapp.checkout.model.dibsapi;


import lombok.Data;

@Data
public class BulkSubscription {

    /**
     * The users subscription id.
     */
    private String subscriptionId;


    /**
     * The subscription order object.
     */
    private DibsOrder dibsOrder;

}
