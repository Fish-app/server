package no.fishapp.checkout.model.dibsapi;


import lombok.Data;

@Data
public class SubscriptionInfo {

    private String endDate;

    /**
     * Defines the minimum number of days between each recurring charge.
     */
    private int interval;
}
