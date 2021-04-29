package no.fishapp.store.model.rating;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.transaction.Transaction;

import javax.persistence.*;

/**
 * Represents a Rating.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    //Id of the rating
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    //Id of the user that created the rating
    long issuerId;

    //Id of the user that did the rating
    long userRatedId;

    @ManyToOne
    Transaction ratedTransactions;

    //Value of the rating
    int stars;
}
