package no.fishapp.app.listing.entity;


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

    //Amount you want to buy
    @Column(nullable = false, name = "amount")
    int amount;

    //Additional info about the request
    String info;

    //Maximum distance you want to travel
    double maxDistance;

}
