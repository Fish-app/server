package no.fishapp.store.model.listing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.validators.HasValidId;
import no.fishapp.user.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Listing {

    /**
     * Id of the listing
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Time the listing was created
     */
    @Column(nullable = false, name = "created")
    private Long created;

    /**
     * Id of the user that create the listing
     */
    private long creatorId;


    /*
     this is a consequence of not great app (as in the flutter app) design. The app should not bring data between pages
     and should rather pass the id of the object to display. In doing so the app pages wold be independent and not have
     to relay on a future fetched 3 screens back.

     But there is not enough time for me to fix this as well so mimicking the previous destruct will have to work.
     and yes, the User module should probably not have been separated out from the store module.
     */
    @Transient
    private User creator;

    public abstract String getListingType();

    /**
     * End date of a listing
     */
    @NotNull
    @Column(nullable = false, name = "end_date")
    long endDate;

    /**
     * Price of the listing
     */
    @NotNull(message = "Price can not be null")
    @Positive
    @Column(nullable = false)
    private Double price;

    /**
     * Status if the listing is open or closed
     */
    @Column(name = "is_open")
    Boolean isOpen;

    /**
     * The coordinates for the pickup point
     */
    @Column(nullable = false, name = "latitude")
    double latitude;
    @Column(nullable = false, name = "longitude")
    double longitude;

    @NotNull()
    @HasValidId
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Commodity commodity;


    @PrePersist
    protected void onCreate() {
        created = System.currentTimeMillis() / 1000L;
        isOpen = true;
    }

}
