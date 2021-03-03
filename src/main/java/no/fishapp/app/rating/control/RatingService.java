package no.fishapp.app.rating.control;

import no.fishapp.app.rating.entity.Rating;
import no.fishapp.app.transaction.control.TransactionService;
import no.fishapp.app.transaction.entity.Transaction;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Random;

public class RatingService {


    @Inject
    TransactionService transactionService;

    @Inject
    UserService userService;

    @PersistenceContext
    EntityManager entityManager;


    private static final String RATING_EXISTS_QUERY = "select count (rt) from Rating  rt where  rt.issuer.id = :isu_id and  rt.userRated.id = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String USER_RATING = "select avg (rt.stars) from Rating rt where rt.userRated.id = :rtd_id";


    public Rating newRating(long transactionId, int ratingValue) {
        Rating      rating      = new Rating();
        Transaction transaction = transactionService.getTransaction(transactionId);
        User        user        = userService.getLoggedInUser();

        boolean isMaker = transaction.getMaker().getId() == user.getId();
        boolean isTaker = transaction.getTaker().getId() == user.getId();

        if (isMaker || isTaker) {
            User ratingReciver = isMaker ? transaction.getMaker() : transaction.getTaker();

            if (! doRatingExists(transactionId, ratingReciver.getId(), transaction.getId())) {
                rating.setIssuer(user);
                rating.setUserRated(ratingReciver);
                rating.setStars(ratingValue);
                rating.setRatedTransactions(transaction);
                entityManager.persist(rating);
                return rating;
            }
        }
        return null;

    }

    boolean doRatingExists(long transId, long issuId, long ratedId) {
        Query query = entityManager.createQuery(RATING_EXISTS_QUERY);
        query.setParameter("isu_id", issuId);
        query.setParameter("rtd_id", ratedId);
        query.setParameter("t_id", transId);
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
