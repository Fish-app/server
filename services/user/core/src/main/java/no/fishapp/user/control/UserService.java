package no.fishapp.user.control;

import no.fishapp.user.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class UserService {

    @PersistenceContext
    EntityManager entityManager;


    private static final String GET_USERS_FROM_ID_LIST = "select us from User us where  us.id in :ids";

    public List<User> getUserFromIdList(List<Long> idList) {
        var query = entityManager.createQuery(GET_USERS_FROM_ID_LIST, User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
    }

}
