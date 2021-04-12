package no.fishapp.store.model.rating;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.transaction.Transaction;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rating {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long issuerId;

    long userRatedId;

    @ManyToOne
    Transaction ratedTransactions;

    int stars;
}
