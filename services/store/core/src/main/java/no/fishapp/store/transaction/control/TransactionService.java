package no.fishapp.store.transaction.control;


import io.jsonwebtoken.Claims;
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
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;

public class TransactionService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ListingService listingService;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    SecurityContext securityContext;

    private static final String RATING_EXISTS_QUERY = "select ts from Transaction ts where ts.sellerId = :uid or ts.buyerId = :uid";


    public List<Transaction> getUserTransactions() {
        if (jwtSubject.get().isEmpty()) {
            return null;
        }

        var query = entityManager.createQuery(RATING_EXISTS_QUERY, Transaction.class);
        query.setParameter("uid", jwtSubject.get().get());

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
        }
        return null;
    }


    public Transaction getTransaction(long id) {
        return entityManager.find(Transaction.class, id);
    }

    public Transaction newTransaction(StartTransactionData transactionData) {
        Listing listing = listingService.findListingById(transactionData.getListingId());

        // TODO: if seller init this is a issue
        if (listing != null && !securityContext.isUserInRole(Group.SELLER_GROUP_NAME) && jwtSubject.get().isPresent()) {
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionData.getAmount());
            transaction.setPrice(listing.getPrice());
            transaction.setSellerId(listing.getCreatorId());
            transaction.setBuyerId(Long.parseLong(jwtSubject.get().get()));
            transaction.setListing(listing);
            entityManager.persist(transaction);
            return transaction;

        }
        return null;
    }

}
