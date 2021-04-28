package no.fishapp.checkout.model.dibsapi;

import lombok.Data;
import no.fishapp.checkout.model.SubscribedUser;
import no.fishapp.checkout.model.enums.Currencies;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class DibsOrder {

    @Id
    @JsonbTransient
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The items to charge for in the order.
     */
    @ManyToMany
    private List<Item> items;

    /**
     * The total amount of currency to pay.
     * is in hundredths i.e. 10000 is interpeted as "100.00"
     * must be equal to the sum of all gross total amount for all objects
     */
    private int amount;

    /**
     * The currency to pay in
     */
    @Enumerated(EnumType.STRING)
    private Currencies currency;

    /**
     * A unique payment reference.
     */
    private String reference;


    @JsonbTransient
    private DibsPaymentStatus dibsPaymentStatus;

    @JsonbTransient
    @ManyToOne
    private SubscribedUser orderOwner;

    public void calculateAndSetAmount() {
        amount = items.stream().reduce(0, (integer, item) -> integer + item.getGrossTotalAmount(), Integer::sum);
    }

}

