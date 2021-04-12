package no.fishapp.store.rating.control;


import io.jsonwebtoken.Claims;
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
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;
import java.util.Random;

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
    SecurityContext securityContext;


    private static final String RATING_EXISTS_QUERY = "select count (rt) from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String GET_TRANSACT_RATING_QUERY = "select rt from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String USER_RATING = "select avg (rt.stars) from Rating rt where rt.userRatedId = :rtd_id";


    public Rating newRating(long transactionId, int ratingValue) {
        if (jwtSubject.get().isEmpty()) {
            return null;
        }
        Rating      rating        = new Rating();
        Transaction transaction   = transactionService.getTransaction(transactionId);
        long        currentUserId = Long.parseLong(jwtSubject.get().get());
        boolean     isSeller      = securityContext.isUserInRole(Group.SELLER_GROUP_NAME);
        boolean isInTransaction = (isSeller) ?
                                  transaction.getSellerId() == currentUserId :
                                  transaction.getBuyerId() == currentUserId;


        if (isInTransaction) {
            long ratingReceiverId = isSeller ? transaction.getBuyerId() : transaction.getSellerId();

            if (!doRatingExists(transactionId, currentUserId, ratingReceiverId)) {
                rating.setIssuerId(currentUserId);
                rating.setUserRatedId(ratingReceiverId);
                rating.setStars(ratingValue);
                rating.setRatedTransactions(transaction);
                entityManager.persist(rating);
                return rating;
            }
        }
        return null;

    }

    public Rating getTransactionRating(long transactionId) {
        if (jwtSubject.get().isEmpty()) {
            return null;
        }
        Rating      rating        = new Rating();
        Transaction transaction   = transactionService.getTransaction(transactionId);
        long        currentUserId = Long.parseLong(jwtSubject.get().get());
        boolean     isSeller      = securityContext.isUserInRole(Group.SELLER_GROUP_NAME);
        boolean isInTransaction = (isSeller) ?
                                  transaction.getSellerId() == currentUserId :
                                  transaction.getBuyerId() == currentUserId;


        if (isInTransaction) {
            long ratingReceiverId = isSeller ? transaction.getBuyerId() : transaction.getSellerId();

            return getRating(transactionId, currentUserId, ratingReceiverId);

        }
        return null;
    }

    Rating getRating(long transId, long issuId, long ratedId) {
        var query = entityManager.createQuery(GET_TRANSACT_RATING_QUERY, Rating.class);

        query.setParameter("isu_id", issuId);
        query.setParameter("rtd_id", ratedId);
        query.setParameter("t_id", transId);
        try {
            return query.getSingleResult();

        } catch (NoResultException ignore) {
        }
        return null;
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


    public float getUserRating(long userId) {
        Query query = entityManager.createQuery(USER_RATING);
        query.setParameter("rtd_id", userId);
        try {
            return (float) query.getSingleResult();

        } catch (Exception ignore) {
            //TODO: make return zero before prod
            Random r = new Random();
            return r.nextFloat() * 5;
        }


        ///return 0F;

    }
}
