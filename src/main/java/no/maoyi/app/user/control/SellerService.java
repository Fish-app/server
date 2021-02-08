package no.***REMOVED***.app.user.control;

import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.Seller;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


public class SellerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;


    /**
     * Makes the logged inn user a seller and persists it to the db
     *
     * @param regNumber TODO: fill in
     *
     * @return the created seller
     */
    public Seller createSeller(String regNumber) {
        User user = userService.getLoggedInUser();
        user.getGroups().add(entityManager.find(Group.class, Group.SELLER_GROUP_NAME));
        entityManager.persist(user);

        Seller newSeller = new Seller(user, regNumber);
        entityManager.persist(newSeller);

        return newSeller;
    }

    /**
     * Returns the currently logged inn seller
     *
     * @return the seller objet
     */
    public Seller getLoggedInSeller() {
        User user = userService.getLoggedInUser();
        return entityManager.find(Seller.class, user.getId());
    }
}
