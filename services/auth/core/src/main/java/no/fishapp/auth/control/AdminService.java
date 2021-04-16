package no.fishapp.auth.control;


import no.fishapp.auth.model.AuthenticatedUser;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequestScoped
public class AdminService {

    private static final String ALL_USERS = "select us from AuthenticatedUser us";

    @Inject
    AuthenticationService authenticationService;
    @PersistenceContext
    EntityManager entityManager;
    @Inject
    PasswordHash hasher;

    /**
     * Changes the password for the provided user id
     *
     * @param userId      the id to change
     * @param newPassword the new password for the id
     *
     * @return true if user is found and password changed otherwise false
     */
    public boolean changeUserPassword(long userId, String newPassword) {
        boolean                     suc          = false;
        Optional<AuthenticatedUser> userOptional = authenticationService.getUserFromId(userId);
        if (userOptional.isPresent()) {
            AuthenticatedUser user = userOptional.get();
            user.setPassword(hasher.generate(newPassword.toCharArray()));
            entityManager.merge(user);
            suc = true;
        }
        return suc;
    }


    public List<AuthenticatedUser> getAllAuthUsers() {
        var query = entityManager.createQuery(ALL_USERS, AuthenticatedUser.class);

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
        }
        return new ArrayList<>();
    }


}
