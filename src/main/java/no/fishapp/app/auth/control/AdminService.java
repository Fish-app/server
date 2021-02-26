package no.fishapp.app.auth.control;

import no.fishapp.app.auth.entity.AuthenticatedUser;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;

public class AdminService {
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
        boolean           suc  = false;
        AuthenticatedUser user = authenticationService.getUserFromId(userId);
        if (user != null) {
            user.setPassword(hasher.generate(newPassword.toCharArray()));
            entityManager.merge(user);
            suc = true;
        }
        return suc;
    }

}
