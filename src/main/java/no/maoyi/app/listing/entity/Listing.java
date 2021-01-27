package no.***REMOVED***.app.listing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.commodity.entity.Commodity;
import no.***REMOVED***.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public abstract class Listing {

    protected String listingType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    @ManyToOne(cascade = CascadeType.ALL)
    User creator;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "end_date")
    Date endDate;

    @ManyToOne
    @JsonbTransient
    Commodity commodity;

    @Column(nullable = false)
    int price;

    @Column(name = "is_open")
    Boolean isOpen;


    @PrePersist
    protected void onCreate() {
        created = new Date();
    }


}
