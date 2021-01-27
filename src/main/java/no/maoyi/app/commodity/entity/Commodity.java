package no.maoyi.app.commodity.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.listing.entity.OfferListing;
import no.maoyi.app.listing.entity.BuyRequest;
import no.maoyi.app.resources.entity.Image;

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
    Image commodityImage;


    @OneToMany
    @JsonbTransient
    List<OfferListing> offerListings;

    @OneToMany
    @JsonbTransient
    List<BuyRequest> orderListings;


}
