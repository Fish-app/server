package no.fishapp.store.transaction.control;


import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.listing.control.ListingService;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.store.model.transaction.DTO.StartTransactionData;
import no.fishapp.store.model.transaction.Transaction;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;


@Log
public class TransactionService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ListingService listingService;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    @Claim("groups")
    Instance<Optional<HashSet<String>>> jwtGroups;

    private static final String RATING_EXISTS_QUERY = "select ts from Transaction ts where ts.sellerId = :uid or ts.buyerId = :uid";


    public List<Transaction> getUserTransactions() {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return new ArrayList<>(); // ehhh
        }

        long currentUserId = Long.parseLong(jwtSubject.get()
                                                      .get());

        var query = entityManager.createQuery(RATING_EXISTS_QUERY, Transaction.class);
        query.setParameter("uid", currentUserId);

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
        }
        return new ArrayList<>();
    }


    public Optional<Transaction> getTransaction(long id) {
        return Optional.ofNullable(entityManager.find(Transaction.class, id));
    }

    public Optional<Transaction> newTransaction(StartTransactionData transactionData) {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty(); // ehhh
        }

        long currentUserId = Long.parseLong(jwtSubject.get()
                                                      .get());

        Optional<Listing> listingOptional = listingService.findListingById(transactionData.getListingId());
        HashSet<String>   groups          = jwtGroups.get().get();

        // TODO: if seller init this is a issue
        if (!groups.contains(Group.SELLER_GROUP_NAME) && listingOptional.isPresent()) {
            Listing     listing     = listingOptional.get();
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionData.getAmount());
            transaction.setPrice(listing.getPrice());
            transaction.setSellerId(listing.getCreatorId());
            transaction.setBuyerId(currentUserId);
            transaction.setListing(listing);
            entityManager.persist(transaction);
            return Optional.of(transaction);

        }
        return Optional.empty();
    }

}
