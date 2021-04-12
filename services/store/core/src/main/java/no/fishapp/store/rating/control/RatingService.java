package no.fishapp.store.rating.control;


import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.model.rating.Rating;
import no.fishapp.store.model.transaction.Transaction;
import no.fishapp.store.transaction.control.TransactionService;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;

@Log
public class RatingService {


    @Inject
    TransactionService transactionService;

    //@Inject
    //UserService userService;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;


    @Inject
    @Claim("groups")
    Instance<Optional<HashSet<String>>> jwtGroups;


    private static final String RATING_EXISTS_QUERY = "select count (rt) from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String GET_TRANSACT_RATING_QUERY = "select rt from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String USER_RATING = "select avg (rt.stars) from Rating rt where rt.userRatedId = :rtd_id";


    public Optional<Rating> newRating(long transactionId, int ratingValue) {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long currentUserId = Long.parseLong(jwtSubject.get().get());

        HashSet<String> groups = jwtGroups.get().get();


        Rating                rating              = new Rating();
        Optional<Transaction> transactionOptional = transactionService.getTransaction(transactionId);
        boolean               isSeller            = groups.contains(Group.SELLER_GROUP_NAME);


        if (transactionOptional.map(transaction -> transaction.isUserInTransaction(currentUserId))
                               .orElse(false)) {
            Transaction transaction      = transactionOptional.get();
            long        ratingReceiverId = isSeller ? transaction.getBuyerId() : transaction.getSellerId();

            if (!doRatingExists(transactionId, currentUserId, ratingReceiverId)) {
                rating.setIssuerId(currentUserId);
                rating.setUserRatedId(ratingReceiverId);
                rating.setStars(ratingValue);
                rating.setRatedTransactions(transaction);
                entityManager.persist(rating);
                return Optional.of(rating);
            }
        }
        return Optional.empty();

    }

    public Optional<Rating> getTransactionRating(long transactionId) {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long currentUserId = Long.parseLong(jwtSubject.get().get());

        HashSet<String> groups = jwtGroups.get().get();


        Rating                rating              = new Rating();
        Optional<Transaction> transactionOptional = transactionService.getTransaction(transactionId);
        boolean               isSeller            = groups.contains(Group.SELLER_GROUP_NAME);

        if (transactionOptional.map(transaction -> transaction.isUserInTransaction(currentUserId))
                               .orElse(false)) {
            Transaction transaction      = transactionOptional.get();
            long        ratingReceiverId = isSeller ? transaction.getBuyerId() : transaction.getSellerId();
            return getRating(transactionId, currentUserId, ratingReceiverId);
        }
        return Optional.empty();
    }

    Optional<Rating> getRating(long transId, long issuId, long ratedId) {
        var query = entityManager.createQuery(GET_TRANSACT_RATING_QUERY, Rating.class);

        query.setParameter("isu_id", issuId);
        query.setParameter("rtd_id", ratedId);
        query.setParameter("t_id", transId);
        try {
            return Optional.of(query.getSingleResult());

        } catch (NoResultException ignore) {
        }
        return Optional.empty();
    }


    boolean doRatingExists(long transactionId, long issuId, long ratedId) {
        Query query = entityManager.createQuery(RATING_EXISTS_QUERY);
        query.setParameter("isu_id", issuId);
        query.setParameter("rtd_id", ratedId);
        query.setParameter("t_id", transactionId);
        try {
            long numWith = (long) query.getSingleResult();
            if (numWith == 0) {
                return false;
            }
        } catch (NoResultException ignore) {
        }
        return true;
    }


    public Optional<Float> getUserRating(long userId) {
        Query query = entityManager.createQuery(USER_RATING);
        query.setParameter("rtd_id", userId);
        try {
            return Optional.ofNullable((Float) query.getSingleResult());

        } catch (NoResultException e) {

//            //TODO: make return zero before prod
//            Random r = new Random();
//            return r.nextFloat() * 5;
        }
        return Optional.empty();

        ///return 0F;

    }
}
