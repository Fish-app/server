package no.fishapp.user.control;

import no.fishapp.user.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AdminUserInfoService {

    private static final String ALL_USERS = "select us from User us";


    @PersistenceContext
    EntityManager entityManager;


    public List<User> getAllUsers() {
        var query = entityManager.createQuery(ALL_USERS, User.class);

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
        }
        return new ArrayList<>();
    }


}
