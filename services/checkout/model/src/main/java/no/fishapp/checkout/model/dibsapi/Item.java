package no.fishapp.checkout.model.dibsapi;

import lombok.Data;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
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
     * Tax rate in 1/100 percentages i.e 2500 = 25%
     */
    private int taxRate;

    /**
     * The amount of VAT/tax
     * is in hundredths i.e. 10000 is interpeted as "100.00"
     */
    private int taxAmount;

    /**
     * Product total amount including VAT
     * is in hundredths i.e. 10000 is interpeted as "100.00"
     */
    private int grossTotalAmount;

    /**
     * Product total amount excluding VAT
     * is in hundredths i.e. 10000 is interpeted as "100.00"
     */
    private int netTotalAmount;
}

