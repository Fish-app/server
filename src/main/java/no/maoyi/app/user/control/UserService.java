package no.maoyi.app.user.control;

import no.maoyi.app.user.entity.Group;
import no.maoyi.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import java.math.BigInteger;

public class UserService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken webToken;

    @Inject
    PasswordHash hasher;

    private static String GET_USER_FROM_EMAIL = "SELECT u FROM User as u WHERE u.email like :mail";


    /**
     * Returns a user from the provided user id
     *
     * @param userId the user id to find the user from
     *
     * @return the user if found null if not
     * @throws NoResultException
     */
    public User getUser(BigInteger userId) throws NoResultException {
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
        return getUser(BigInteger.valueOf(Long.parseLong(webToken.getSubject())));
    }

    /**
     * Returns the user with the provided email as email
     *
     * @param email the email to chek
     *
     * @return The user with the provided email, null if no user with the mail is found
     */
    public User getUserFromEmail(String email) {

        TypedQuery<User> query = entityManager.createQuery(GET_USER_FROM_EMAIL, User.class);
        query.setParameter("mail", "%" + email + "%");
        try {
            return query.getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }
    }


    /**
     * Creates a user with the provided params and persists it to the database
     *
     * @param name     users name
     * @param email    users email
     * @param password users password
     *
     * @return the created user objet
     */
    public User createUser(String name, String email, String password) {

        User  newUser   = new User(email, name, password);
        Group userGroup = entityManager.find(Group.class, Group.USER_GROUP_NAME);
        newUser.setPassword(hasher.generate(password.toCharArray()));
        newUser.getGroups().add(userGroup);

        entityManager.persist(newUser);

        return newUser;

    }
}
