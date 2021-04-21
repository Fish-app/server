package no.fishapp.checkout.model.dibsapi;

import lombok.Data;
import no.fishapp.checkout.model.enums.Currencies;

import java.util.List;

@Data
public class Order {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private double id;

    /**
     * The items to charge for in the order.
     */
    private List<Item> items;

    /**
     * The total amount of currency to pay.
     */
    private int amount;

    /**
     * The currency to pay in
     */
    private Currencies currency;

    /**
     * A unique payment reference.
     */
    private String reference;



    public void calculateAndSetAmount(){
        amount = items.stream().reduce(0, (integer, item) -> integer + item.getGrossTotalAmount(), Integer::sum);
    }

}

