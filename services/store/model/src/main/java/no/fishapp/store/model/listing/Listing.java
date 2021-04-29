package no.fishapp.store.model.listing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.commodity.Commodity;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Listing {

    //Id of the listing
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    //Time the listing was created
    @Column(nullable = false, name = "created")
    long created;

    //Id of the user that create the listing
    long creatorId;

    public abstract String getListingType();

    //End date of a listing
    @Column(nullable = false, name = "end_date")
    long endDate;

    //Price of the listing
    @Column(nullable = false)
    double price;

    //Status if the listing is open or closed
    @Column(name = "is_open")
    Boolean isOpen;

    /**
     * The coordinates for the pickup point
     */
    @Column(nullable = false, name = "latitude")
    double latitude;
    @Column(nullable = false, name = "longitude")
    double longitude;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Commodity commodity;


    @PrePersist
    protected void onCreate() {
        created = System.currentTimeMillis() / 1000L;
        isOpen = true;
    }

}
