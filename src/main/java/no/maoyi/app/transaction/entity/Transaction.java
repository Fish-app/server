package no.maoyi.app.transaction.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    //    @ManyToOne
    //    @JsonbTransient
    //    Listing transactionListing;

    @Column(nullable = false)
    int amount;

    @Column(nullable = false)
    int price;

    @ManyToOne
    User maker;

    @ManyToOne
    User taker;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }


}
