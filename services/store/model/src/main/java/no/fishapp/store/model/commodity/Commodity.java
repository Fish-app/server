package no.fishapp.store.model.commodity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.media.model.Image;
import no.fishapp.store.model.listing.Listing;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Commodity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    String name;

    @OneToOne
    Image commodityImage;

    @OneToMany
    @JsonbTransient
    List<Listing> listings;


}
