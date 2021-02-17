package no.maoyi.app.user.control;

import no.maoyi.app.user.entity.Buyer;
import no.maoyi.app.auth.entity.Group;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.PasswordHash;
import java.math.BigInteger;


public class BuyerService {

    @PersistenceContext
    EntityManager entityManager;
    @Inject
    JsonWebToken webToken;
    @Inject
    PasswordHash hasher;

    /**
     * Returns the logged in buyer - if not logged in or error fetching buyer null is returned
     *
     * @return the logged in buyer or null
     */
    public Buyer getLoggedInBuyer() {
        return getBuyer(Long.parseLong(webToken.getSubject()));
    }

    /**
     * Creates a buyer with the provided params and persists it to the database
     *
     * @param name     buyers name
     * @param email    buyers email
     * @param password buyers password
     *
     * @return the created buyer objet
     */
    public Buyer createBuyer(String name, String email, String password) {

        Buyer newBuyer   = new Buyer(email, name, password);
        Group buyerGroup = entityManager.find(Group.class, Group.BUYER_GROUP_NAME);
        Group userGroup  = entityManager.find(Group.class, Group.USER_GROUP_NAME);
        newBuyer.setPassword(hasher.generate(password.toCharArray()));
        newBuyer.getGroups().add(buyerGroup);
        newBuyer.getGroups().add(userGroup);

        entityManager.persist(newBuyer);

        return newBuyer;

    }

    /**
     * Returns a buyer from the provided buyer id
     *
     * @param buyerId the buyer id to find the buyer from
     *
     * @return the buyer if found null if not
     * @throws NoResultException
     */
    public Buyer getBuyer(long buyerId) throws NoResultException {
        try {
            return entityManager.find(Buyer.class, buyerId);
        } catch (Exception ignored) {
        }
        return null;
    }


}
