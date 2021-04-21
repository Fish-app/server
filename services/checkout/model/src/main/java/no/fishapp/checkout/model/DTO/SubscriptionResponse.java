package no.fishapp.checkout.model.DTO;

import lombok.Data;

@Data
public class SubscriptionResponse {

    private String paymentId;
    private String hostedPaymentPageUrl;


}
