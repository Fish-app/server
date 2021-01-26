package no.maoyi.app.listing.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.maoyi.app.transaction.entity.Transaction;

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

    @OneToMany
    List<Transaction> transactions;


}
