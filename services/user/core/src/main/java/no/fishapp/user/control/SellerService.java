package no.fishapp.user.control;


import com.ibm.websphere.security.jwt.Claims;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.Group;
import no.fishapp.user.client.AuthClient;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import no.fishapp.user.model.user.Buyer;
import no.fishapp.user.model.user.DTO.SellerNewData;
import no.fishapp.user.model.user.Seller;
import no.fishapp.user.model.user.User;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import java.util.List;
import java.util.Optional;


public class SellerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    PasswordHash hasher;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    @RestClient
    AuthClient authClient;

    public Seller createSeller(SellerNewData sellerNewData) throws UsernameAlreadyInUseException {
        var newUserDto = new NewAuthUserData();
        newUserDto.setUserName(sellerNewData.getUserName());
        newUserDto.setPassword(sellerNewData.getPassword());
        newUserDto.setGroups(List.of(no.fishapp.auth.model.Group.USER_GROUP_NAME,
                no.fishapp.auth.model.Group.SELLER_GROUP_NAME
        ));
        var addAuth = authClient.addAuthUser(newUserDto);

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
        //todo:handle pot error
        return jwtSubject.get().map(s -> this.getSeller(Long.parseLong(s))).orElse(null);
    }

    public Seller getSeller(long sellerId) throws NoResultException {
        try {
            return entityManager.find(Seller.class, sellerId);
        } catch (Exception ignored) {
        }
        return null;
    }
}
