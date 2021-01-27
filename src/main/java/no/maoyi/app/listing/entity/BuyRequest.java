package no.maoyi.app.listing.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;


@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuyRequest extends Listing {

    protected String listingType = "order_listing";

    int amount;


}
