package no.fishapp.checkout.model.dibsapi.responses;

import lombok.Data;

@Data
public class SubscriptionResponse {

    private String paymentId;
    private String hostedPaymentPageUrl;


}
