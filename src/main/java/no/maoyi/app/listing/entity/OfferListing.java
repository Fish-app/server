package no.***REMOVED***.app.listing.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.transaction.entity.Transaction;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferListing extends Listing {

    protected String listingType = "offer_listing";

    /**
     * The max ammount the seller is capable of offering
     */
    @Column(nullable = false, name = "max_ammount")
    int maxAmount;

    /**
     * The coordinates for the pickup point
     */
    @Column(nullable = false, name = "latitude")
    double latitude;
    @Column(nullable = false, name = "longitude")
    double longitude;

    // TODO: sjekke om det her blir med som et felt eller om man treng og jør nå constructor shenanagans
    public int getAmountLeft() {
        if (transactions != null) {
            return maxAmount - transactions.stream().map(Transaction::getAmount).reduce(0, Integer::sum);
        }
        return maxAmount;
    }

    @OneToMany
    @JsonbTransient
    List<Transaction> transactions;


}
