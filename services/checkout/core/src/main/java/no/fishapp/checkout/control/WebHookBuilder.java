package no.fishapp.checkout.control;

import no.fishapp.checkout.model.dibsapi.WebHook;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;

public class WebHookBuilder {

    @Inject
    @ConfigProperty(name = "fishapp.checkout.host.url")
    private String hostUrl;


    /**
     * i had made the infrastructure for creating short lived tokens to use for the webhooks,
     * when i discovered they only support up to 32 (or 64 their documentation seem confused an references both as the hard limit)
     * characters for the auth field. i am baffled to why.
     * Ideally these keys should be rotated but doing so outside the current application auth system
     * wold be a headache and yield unnecessary complexity.
     * If you the reader are deploying this in a production setting call tech.dibspayment.com tell them security
     * should kind of be their thing and limiting the auth sizes is stupid on a whole lot of levels.
     * <p>
     * and yes i realise they may intend to for the webhooks to be single use. but if i wanted to kill the db i wold rather use a hammer
     */
    @Inject
    @ConfigProperty(name = "fishapp.checkout.dibs.static.auth.key")
    private String shittyStatufullKey;

    //@Inject
    //DibsLoginTokenManager dibsLoginTokenManager;

    /**
     * When a payment is created.
     * that is when someone enters the payment screen
     */
    public static String EVENT_PAYMENT_CREATED = "payment.created";


    /**
     * When someone has completed the checkout screen
     */
    public static String EVENT_PAYMENT_CHECKOUT_COMPLETE = "payment.checkout.completed";

    /**
     * When someone has completed the pay screen and the card reservation is created
     */
    public static String EVENT_PAYMENT_RESERVATION_CREATED = "payment.reservation.created";


    /**
     * When the payment charge is created. that is sucsessfull payment
     */
    public static String EVENT_PAYMENT_CHARGE_CREATED = "payment.charge.created";

    /**
     * If the charge fails to be created
     */
    public static String EVENT_PAYMENT_CHARGE_FAILED = "payment.charge.failed";


    public WebHook getPaymentCreatedWebhook() {
        WebHook webHook = new WebHook();
        webHook.setUrl(hostUrl + "/api/checkout/webhooks/payment-created");
        webHook.setEventName(EVENT_PAYMENT_CREATED);
        webHook.setAuthorization(shittyStatufullKey);
        return webHook;
    }

    public WebHook getPaymentReservationCreatedWebhook() {
        WebHook webHook = new WebHook();
        webHook.setUrl(hostUrl + "/api/checkout/webhooks/payment-reservation-created");
        webHook.setEventName(EVENT_PAYMENT_RESERVATION_CREATED);
        webHook.setAuthorization(shittyStatufullKey);
        return webHook;
    }

    public WebHook getPaymentChargeCreatedWebhook() {
        WebHook webHook = new WebHook();
        webHook.setUrl(hostUrl + "/api/checkout/webhooks/payment-charge-created");
        webHook.setEventName(EVENT_PAYMENT_CHARGE_CREATED);
        webHook.setAuthorization(shittyStatufullKey);
        return webHook;
    }

    public WebHook getPaymentChargeFailedWebhook() {
        WebHook webHook = new WebHook();
        webHook.setUrl(hostUrl + "/api/checkout/webhooks/payment-charge-failed");
        webHook.setEventName(EVENT_PAYMENT_CHARGE_FAILED);
        webHook.setAuthorization(shittyStatufullKey);
        return webHook;
    }


}
