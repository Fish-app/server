package no.maoyi.app.user.control;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.user.entity.Seller;
import no.maoyi.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class SellerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;


    public Seller createSeller(String name, String email, String password, String regNumber) {
        User user = userService.getLoggedInUser();
        user.getGroups().add(entityManager.find(Group.class, Group.SELLER_GROUP_NAME));
        entityManager.persist(user);

        Seller newSeller = new Seller(name, email, password, regNumber);
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
