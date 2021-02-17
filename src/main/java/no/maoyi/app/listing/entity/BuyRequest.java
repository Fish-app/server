package no.***REMOVED***.app.listing.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * A request for something you want to buy.
 */

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuyRequest extends Listing {

    protected String listingType = "order_listing";

    //Amount you want to buy
    @Column(nullable = false, name = "amount")
    int amount;

    //Additional info about the request
    @Column(name = "info")
    String info;

    //Maximum distance you want to travel
    @Column(name = "maxDistance")
    double maxDistance;

}
