package no.fishapp.checkout.model.dibsapi.responses;


import lombok.Data;

@Data
public class BulkChargeResponseItem {

    private String subscriptionId;
    private String paymentId;
    private String status;

}
