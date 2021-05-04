package no.fishapp.checkout.model.enums;


/**
 * The different states for a subscribed users payment status
 */
public enum SubscriptionPaymentStatus {
    /**
     * The user has payed and the subscription is ok.
     */
    OK,

    /**
     * The user is currently beeing charged for the next period.
     */
    REFRESHING,

    /**
     * The user does not currently have an active subscription.
     */
    NOT_ACTIVE,
}
