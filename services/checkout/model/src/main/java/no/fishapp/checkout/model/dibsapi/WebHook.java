package no.fishapp.checkout.model.dibsapi;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebHook {

    private String eventName;
    private String url;
    private String authorization;

    public static String EVENT_PAY_CHECKOUT_COMPLETE = "payment.checkout.completed";
    public static String EVENT_PAY_CHECKOUT_FAILED = "payment.charge.failed";
    public static String EVENT_SUBSCRIPTION_CHARGE_COMPLETE = "payment.charge.created";


}
