package no.fishapp.store.model.listing;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Listing {

    //protected String listingType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, name = "created")
    long created;

    @ManyToOne
    User creator;

    public abstract String getListingType();

    @Column(nullable = false, name = "end_date")
    long endDate;


    @Column(nullable = false)
    double price;

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
        isOpen  = true;
    }

}
