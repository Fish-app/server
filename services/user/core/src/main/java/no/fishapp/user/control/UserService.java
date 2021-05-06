package no.fishapp.user.control;

import no.fishapp.user.model.user.Seller;
import no.fishapp.user.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    @PersistenceContext
    EntityManager entityManager;


    private static final String GET_USERS_FROM_ID_LIST = "select sl, br from Seller sl, Buyer br where  sl.id in :ids OR br.id in :ids";

    public List<User> getUserFromIdList(List<Long> idList) {
        if (idList != null && idList.size() > 0) {
            var query = entityManager.createQuery(GET_USERS_FROM_ID_LIST, User.class);
            query.setParameter("ids", idList);

            return query.getResultList();
        } else {
            return new ArrayList<>();
        }

    }


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

}
