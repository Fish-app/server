package no.fishapp.app.transaction.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.user.entity.Buyer;
import no.fishapp.app.user.entity.Seller;
import no.fishapp.app.user.entity.User;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long created;

    @Column(nullable = false)
    int amount;

    @Column(nullable = false)
    double price;

    @ManyToOne
    @JoinColumn(nullable = false)
    Seller seller;

    @ManyToOne
    @JoinColumn(nullable = false)
    Buyer buyer;

    @ManyToOne
    Listing listing;

    @PrePersist
    protected void onCreate() {
        created = System.currentTimeMillis() / 1000L;
    }


}
