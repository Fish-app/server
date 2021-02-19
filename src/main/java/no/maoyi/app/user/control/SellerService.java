package no.maoyi.app.user.control;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.user.entity.Seller;
import no.maoyi.app.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;


public class SellerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;

    JsonWebToken webToken;
    @Inject
    PasswordHash hasher;

    public Seller createSeller(String name, String email, String password, String regNumber) {
        Seller newSeller = new Seller(name, email, password, regNumber);

        Group sellerGroup = entityManager.find(Group.class, Group.SELLER_GROUP_NAME);
        Group userGroup  = entityManager.find(Group.class, Group.USER_GROUP_NAME);
        newSeller.setPassword(hasher.generate(password.toCharArray()));
        newSeller.getGroups().add(sellerGroup);
        newSeller.getGroups().add(userGroup);


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
