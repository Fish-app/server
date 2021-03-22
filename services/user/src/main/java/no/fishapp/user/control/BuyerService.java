package no.fishapp.user.control;


import no.fishapp.auth.entity.AuthenticatedUser;
import no.fishapp.auth.entity.DTO.UsernamePasswordData;
import no.fishapp.auth.entity.Group;
import no.fishapp.user.entity.Buyer;
import no.fishapp.user.client.AuthClient;
import no.fishapp.user.entity.DTO.BuyerNewData;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.PasswordHash;

@ApplicationScoped
public class BuyerService {

    @PersistenceContext
    EntityManager entityManager;
    @Inject
    JsonWebToken webToken;

    @Inject
    @RestClient
    AuthClient authClient;

    /**
     * Returns the logged in buyer - if not logged in or error fetching buyer null is returned
     *
     * @return the logged in buyer or null
     */
    public Buyer getLoggedInBuyer() {
        return getBuyer(Long.parseLong(webToken.getSubject()));
    }


    public Buyer createBuyer(BuyerNewData buyerNewData) throws UsernameAlreadyInUseException {
        // not super necesery with future here but when images is implemented they can be prematurly sored while the username is validating
        var addAuth = authClient.addAuthUser(buyerNewData);

        AuthenticatedUser authenticatedUser = addAuth.toCompletableFuture().join();

        if (authenticatedUser == null) {
            throw new UsernameAlreadyInUseException();
        }
        Buyer newBuyer = new Buyer(authenticatedUser.getId(), buyerNewData.getUserName(), buyerNewData.getName());
        entityManager.persist(newBuyer);

        return newBuyer;

    }

    /**
     * Returns a buyer from the provided buyer id
     *
     * @param buyerId the buyer id to find the buyer from
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
