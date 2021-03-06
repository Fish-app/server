package no.fishapp.user.control;


import io.jsonwebtoken.Claims;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.user.client.AuthClient;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import no.fishapp.user.model.user.DTO.SellerNewData;
import no.fishapp.user.model.user.Seller;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import java.util.ArrayList;
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


    private static final String GET_SELLERS_FROM_ID_LIST = "select sl from Seller sl where  sl.id in :ids";

    public List<Seller> getSellersFromIdList(List<Long> idList) {
        if (idList != null && idList.size() > 0) {
            var query = entityManager.createQuery(GET_SELLERS_FROM_ID_LIST, Seller.class);
            query.setParameter("ids", idList);
            return query.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<Seller> getLoggedInSeller() {
        if (jwtSubject.get().isPresent()) {
            long userId = Long.parseLong(jwtSubject.get().get());
            return getSeller(userId);
        } else {
            return Optional.empty();
        }
    }

    public Seller createSeller(SellerNewData sellerNewData) throws UsernameAlreadyInUseException, RestClientHttpException {
        var newUserDto = new NewAuthUserData();
        newUserDto.setUserName(sellerNewData.getUserName());
        newUserDto.setPassword(sellerNewData.getPassword());
        newUserDto.setGroups(List.of(no.fishapp.auth.model.Group.USER_GROUP_NAME,
                                     no.fishapp.auth.model.Group.SELLER_GROUP_NAME));
        var addAuth = authClient.addAuthUser(newUserDto);

        AuthenticatedUser authenticatedUser = addAuth.toCompletableFuture().join();

        if (authenticatedUser == null) {
            throw new UsernameAlreadyInUseException();
        }
        Seller newSeller = new Seller(authenticatedUser.getId(),
                                      sellerNewData.getUserName(),
                                      sellerNewData.getName(),
                                      sellerNewData.getRegNumber());
        entityManager.persist(newSeller);

        return newSeller;
    }


    public Optional<Seller> getSeller(long sellerId) throws NoResultException {
        try {
            return Optional.of(entityManager.find(Seller.class, sellerId));
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }
}
