package no.fishapp.user.control;


import no.fishapp.user.model.user.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class UserService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken webToken;


    /**
     * Returns a user from the provided user id
     *
     * @param userId the user id to find the user from
     * @return the user if found null if not
     * @throws NoResultException
     */
    public User getUser(long userId) throws NoResultException {
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public User getLoggedInUser() {
        return getUser(Long.parseLong(webToken.getSubject()));
    }


}
