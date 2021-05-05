package no.fishapp.store.model.listing;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

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
    @NotNull
    @Column(nullable = false, name = "amount")
    Integer amount;

    /**
     * Additional info about the request
     */
    String info;

    /**
     * Maximum distance you want to travel
     */
    double maxDistance;
    
}
