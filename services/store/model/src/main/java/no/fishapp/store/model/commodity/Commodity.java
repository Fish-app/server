package no.fishapp.store.model.commodity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.media.model.Image;
import no.fishapp.store.model.listing.Listing;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Commodity implements Serializable {

    /**
     * Unique id for each Commodity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The name of the Commodity
     */
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    String name;

    /**
     * The image of the Commodity
     */
    @NotNull
    @OneToOne
    Image commodityImage;

    /**
     * The listings a commodity is associated with
     */
    @OneToMany
    @JsonbTransient
    List<Listing> listings;


}
