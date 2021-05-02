package no.fishapp.store.model.transaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.listing.Listing;

import javax.persistence.*;

/**
 * Represents a transaction in the system
 */
@Data
@Entity
@NoArgsConstructor
public class Transaction {
    /**
     * Id of the transaction
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * Time the transaction was created
     */
    long created;

    /**
     * Amount that was bought in the transaction
     */
    @Column(nullable = false)
    int amount;

    /**
     * Price the item was bought for
     */
    @Column(nullable = false)
    double price;

    /**
     * Id of the {@link no.fishapp.user.model.user.Seller} in the transaction
     */
    long sellerId;

    /**
     * Id of the {@link no.fishapp.user.model.user.Buyer} in the transaction
     */
    long buyerId;

    /**
     * {@link Listing} the transaction is associated with
     */
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
