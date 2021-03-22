package no.fishapp.store.model.rating;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.transaction.transaction.Transaction;
import no.fishapp.app.user.entity.User;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rating {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    User issuer;

    @ManyToOne
    User userRated;

    @ManyToOne
    Transaction ratedTransactions;

    int stars;
}
