package no.fishapp.user.control;


import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.chat.model.user.Buyer;
import no.fishapp.chat.model.user.DTO.SellerNewData;
import no.fishapp.chat.model.user.Seller;
import no.fishapp.chat.model.user.User;
import no.fishapp.user.client.AuthClient;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import java.util.List;


public class SellerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;

    @Inject
    PasswordHash hasher;

    @Inject
    @RestClient
    AuthClient authClient;

    public Seller createSeller(SellerNewData sellerNewData) throws UsernameAlreadyInUseException {
        var addAuth = authClient.addAuthUser(sellerNewData,
                                             List.of(no.fishapp.auth.model.Group.USER_GROUP_NAME,
                                                     no.fishapp.auth.model.Group.SELLER_GROUP_NAME
                                             )
        );


        AuthenticatedUser authenticatedUser = addAuth.toCompletableFuture().join();

        if (authenticatedUser == null) {
            throw new UsernameAlreadyInUseException();
        }
        Seller newSeller = new Seller(authenticatedUser.getId(),
                                      sellerNewData.getUserName(),
                                      sellerNewData.getName(),
                                      sellerNewData.getRegNumber()
        );
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

    public Seller getSeller(long sellerId) throws NoResultException {
        try {
            return entityManager.find(Seller.class, sellerId);
        } catch (Exception ignored) {
        }
        return null;
    }
}
