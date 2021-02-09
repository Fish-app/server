package no.***REMOVED***.app.user.control;

import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.Seller;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.*;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationService {

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @PersistenceContext
    EntityManager entityManager;


    @Inject
    PasswordHash hasher;

    @Inject
    UserService userService;


    /**
     * Util method checks if the auth {@link CredentialValidationResult} result is valid
     *
     * @param result
     *
     * @return
     */
    public boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    /**
     * Util method, gets the current {@link CredentialValidationResult}
     *
     * @param userId   Users id
     * @param password Users password
     *
     * @return the credential val result for this username pass combo
     */
    public CredentialValidationResult getValidationResult(BigInteger userId, String password) {
        return identityStoreHandler.validate(new UsernamePasswordCredential(String.valueOf(userId), password));
    }

    /**
     * Changes the password for the logged inn user.
     *
     * @param newPassword the new password to set.
     */
    @Transactional
    public void ChangePassword(String newPassword) {
        User user = userService.getLoggedInUser();
        user.setPassword(hasher.generate(newPassword.toCharArray()));
        entityManager.merge(user);
    }


}
