package no.fishapp.app.commodity.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.resources.entity.Image;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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
