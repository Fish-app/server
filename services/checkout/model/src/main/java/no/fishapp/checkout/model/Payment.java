package no.fishapp.checkout.model;

import lombok.Data;
import no.fishapp.checkout.model.enums.PaymentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@Entity
public class Payment {

    @Id
    @NotNull
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private double userId;

    private String paymentId;

    private PaymentStatus paymentStatus;
}
