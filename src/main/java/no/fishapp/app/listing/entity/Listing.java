package no.fishapp.app.listing.entity;

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
    
    protected String listingType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, name = "created")
    long created;

    @ManyToOne
    User creator;

    @Column(nullable = false, name = "end_date")
    long endDate;

    @ManyToOne
    @JsonbTransient
    Commodity commodity;

    @Column(nullable = false)
    double price;

    @Column(name = "is_open")
    Boolean isOpen;


    @PrePersist
    protected void onCreate() {
        created = System.currentTimeMillis() / 1000L;
        isOpen  = true;
    }

}
