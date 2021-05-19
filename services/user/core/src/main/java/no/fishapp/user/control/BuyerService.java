package no.fishapp.user.control;


import io.jsonwebtoken.Claims;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.Group;
import no.fishapp.user.client.AuthClient;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import no.fishapp.user.model.user.Buyer;
import no.fishapp.user.model.user.DTO.BuyerNewData;
import no.fishapp.user.model.user.Seller;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BuyerService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    @RestClient
    AuthClient authClient;


    private static final String GET_BUYERS_FROM_ID_LIST = "select by from Buyer by where  by.id in :ids";

    public List<Seller> getBuyersFromIdList(List<Long> idList) {
        if (idList != null && idList.size() > 0) {
            var query = entityManager.createQuery(GET_BUYERS_FROM_ID_LIST, Seller.class);
            query.setParameter("ids", idList);
            return query.getResultList();
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * Returns the logged in buyer - if not logged in or error fetching buyer null is returned
     *
     * @return the logged in buyer or null
     */
    public Optional<Buyer> getLoggedInBuyer() {
        if (jwtSubject.get().isPresent()) {
            long userId = Long.parseLong(jwtSubject.get().get());
            return getBuyer(userId);
        } else {
            return Optional.empty();
        }
    }


    public Buyer createBuyer(BuyerNewData buyerNewData) throws UsernameAlreadyInUseException, RestClientHttpException {
        var newUserDto = new NewAuthUserData();
        newUserDto.setUserName(buyerNewData.getUserName());
        newUserDto.setPassword(buyerNewData.getPassword());
        newUserDto.setGroups(List.of(Group.USER_GROUP_NAME, Group.BUYER_GROUP_NAME));
        var addAuth = authClient.addAuthUser(newUserDto);

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
    public Optional<Buyer> getBuyer(long buyerId) throws NoResultException {
        try {
            return Optional.of(entityManager.find(Buyer.class, buyerId));
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }


}
