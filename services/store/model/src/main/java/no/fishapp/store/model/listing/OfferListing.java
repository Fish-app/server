package no.fishapp.store.model.listing;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.transaction.Transaction;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OfferListing extends Listing {

    @Override
    public String getListingType() {
        return "Offer";
    }

    /**
     * The max ammount the seller is capable of offering
     */
    @Column(nullable = false, name = "max_ammount")
    int maxAmount;


    String additionalInfo;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    //@JsonbTransient
    @JoinTable(
            name = "offer_listing_commodity",
            joinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "commodity_id", referencedColumnName = "id"))
    @NotNull
    Commodity commodity;
}
