package no.***REMOVED***.app.commodity.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.listing.entity.OfferListing;
import no.***REMOVED***.app.listing.entity.BuyRequest;
import no.***REMOVED***.app.resources.entity.Image;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Commodity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @Column(nullable = false, columnDefinition = "TEXT")
    String name;

    @OneToOne
    @Column(name = "commodity_image")
    Image commodityImage;


    @OneToMany
    @JsonbTransient
    List<OfferListing> offerListings;

    @OneToMany
    @JsonbTransient
    List<BuyRequest> orderListings;


}
