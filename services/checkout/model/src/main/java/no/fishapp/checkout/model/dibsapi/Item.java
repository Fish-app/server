package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Item {

    @Id
    @JsonbTransient
    private String id;

    /**
     * Product reference
     */
    private String reference;

    /**
     * Product name
     */
    private String name;

    /**
     * product quantity
     */
    private float quantity;

    /**
     * Unit e.g. pcs, Kg, liters, units
     */
    private String unit;

    /**
     * Price for one unit
     */
    private int unitPrice;

    /**
     * Tax rate in percentages that is 10 = 10%
     */
    private int taxRate;

    /**
     * The amount of VAT/tax
     */
    private int taxAmount;

    /**
     * Product total amount including VAT
     */
    private int grossTotalAmount;

    /**
     * Product total amount excluding VAT
     */
    private int netTotalAmount;
}


/*
            "reference":"Reference3",
            "name":"Test Subscription",
            "quantity":1.0,
            "unit":"unit",
            "unitPrice":10000,
            "taxRate":10,
            "taxAmount":200,
            "grossTotalAmount":10000,
            "netTotalAmount":300



 */