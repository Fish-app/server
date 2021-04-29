package no.fishapp.store.model.listing;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.commodity.Commodity;

import javax.persistence.*;

/**
 * A request for something you want to buy.
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BuyRequest extends Listing {

    @Override
    public String getListingType() {
        return "Request";
    }

    /**
     * Amount you want to buy
     */
    @Column(nullable = false, name = "amount")
    int amount;

    /**
     * Additional info about the request
     */
    String info;

    /**
     * Maximum distance you want to travel
     */
    double maxDistance;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    @JoinTable(
            name = "buy_request_commodity",
            joinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "commodity_id", referencedColumnName = "id"))
    @EqualsAndHashCode.Exclude
    private Commodity commodity;
}
