package no.fishapp.store.model.transaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.listing.Listing;

import javax.persistence.*;

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


    long sellerId;


    long buyerId;

    @ManyToOne
    Listing listing;

    @PrePersist
    protected void onCreate() {
        created = System.currentTimeMillis() / 1000L;
    }

    public boolean isUserInTransaction(long userId) {
        return sellerId == userId || buyerId == userId;
    }


}
