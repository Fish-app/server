package no.fishapp.store.rating.control;


import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.model.rating.Rating;
import no.fishapp.store.model.transaction.Transaction;
import no.fishapp.store.transaction.control.TransactionService;
import no.fishapp.user.model.user.User;
import no.fishapp.util.exceptionmappers.NoJwtTokenException;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Manages adding and getting {@link Rating} .
 * Uses {@link EntityManager} to communicate with the database.
 */
@Log
public class RatingService {


    @Inject
    TransactionService transactionService;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    @Claim("groups")
    Instance<Optional<HashSet<String>>> jwtGroups;

    /**
     * Returns the amount of {@link Rating} that exists with {@code issuerId} equals 'isu_id', {@code userRatedId}
     * equals 'rtd_id', and {@code ratedTransactionsId} equals 't_id'.
     */
    private static final String RATING_EXISTS_QUERY = "select count (rt) from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    /**
     * Returns the {@link Rating} where {@code issuerId} equals 'isu_id', {@code userRatedId} equals 'rtd_id',
     * and {@code ratedTransactionsId} equals 't_id'.
     */
    private static final String GET_TRANSACT_RATING_QUERY = "select rt from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    /**
     * Returns the average {@link Rating} where {@code userRatedId} equals 'rtd_id'.
     */
    private static final String USER_RATING = "select avg (rt.stars) from Rating rt where rt.userRatedId = :rtd_id";

    /**
     * Creates a new {@link Rating} connected to the {@link Transaction} matching the transactionId argument.
     * The {@code Rating} get the value from the ratingValue argument. Returns an {@link Optional} containing the
     * resulting {@code Rating}.
     * <p>
     * If the user tries to rate a transaction (s)he is not a member of a {@code empty} Optional is returned
     *
     * @param transactionId the id of the {@code Transaction} to connect the {@code Rating} to
     * @param ratingValue   the value of the {@code Rating}
     * @return an {@code Optional} containing the created {@code Rating} if successful or {@code empty} if not
     */
    public Optional<Rating> newRating(long transactionId, int ratingValue) {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            throw new NoJwtTokenException();
        }

        long currentUserId = Long.parseLong(jwtSubject.get().get());

        HashSet<String> groups = jwtGroups.get().get();


        Rating                rating              = new Rating();
        Optional<Transaction> transactionOptional = transactionService.getTransaction(transactionId);
        boolean               isSeller            = groups.contains(Group.SELLER_GROUP_NAME);


        if (transactionOptional.map(transaction -> transaction.isUserInTransaction(currentUserId)).orElse(false)) {
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

    /**
     * Returns an {@link Optional} containing the {@link Rating} of the {@link Transaction} with an id matching the
     * transactionId argument.
     *
     * @param transactionId the id of the {@code Transaction} to get the {@code Rating} of
     * @return an {@code Optional} containing the {@code Rating} if one is found or {@code empty} if not
     */
    public Optional<Rating> getTransactionRating(long transactionId) {
        if (jwtSubject.get().isEmpty() || jwtGroups.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long currentUserId = Long.parseLong(jwtSubject.get().get());

        HashSet<String> groups = jwtGroups.get().get();


        Optional<Transaction> transactionOptional = transactionService.getTransaction(transactionId);
        boolean               isSeller            = groups.contains(Group.SELLER_GROUP_NAME);

        if (transactionOptional.map(transaction -> transaction.isUserInTransaction(currentUserId)).orElse(false)) {
            Transaction transaction      = transactionOptional.get();
            long        ratingReceiverId = isSeller ? transaction.getBuyerId() : transaction.getSellerId();
            return getRating(transactionId, currentUserId, ratingReceiverId);
        }
        return Optional.empty();
    }

    /**
     * Returns an {@link Optional} containing the {@link Rating} matching the arguments transId, issuId, and ratedId.
     *
     * @param transId the transactionId of the {@code Rating} to get
     * @param issuId  the issuerId of the {@code Rating} to get
     * @param ratedId the userRatedId of the {@code Rating} to get
     * @return an {@code Optional} containing the {@code Rating} if found or {@code empty} if not
     */
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

    /**
     * Checks if a {@link Rating} matching the arguments transactionId, issuId, and ratedId exists.
     *
     * @param transactionId the transactionId of the {@code Rating} to find
     * @param issuId        the issuerId of the {@code Rating} to find
     * @param ratedId       the userRatedId of the {@code Rating} to find
     * @return true of a {@code Rating} with the given arguments exists or false if not
     */
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

    /**
     * Returns an {@link Optional} containing the average {@link Rating} for a {@link User} with an id matching
     * the userId argument.
     *
     * @param userId the id of the {@code User} to get the {@code Rating} of
     * @return an {@code Optional} containing the average {@code Rating} if found or {@code empty} if not
     */
    public Optional<Float> getUserRating(long userId) {
        Query query = entityManager.createQuery(USER_RATING);
        query.setParameter("rtd_id", userId);
        try {
            return Optional.ofNullable((Float) query.getSingleResult());

        } catch (NoResultException e) {
        }
        return Optional.empty();
    }
}
