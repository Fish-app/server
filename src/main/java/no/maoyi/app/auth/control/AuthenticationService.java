package no.***REMOVED***.app.auth.control;

import no.***REMOVED***.app.auth.entity.AuthenticatedUser;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.*;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.math.BigInteger;

@Transactional
public class AuthenticationService {

    final static String GET_USER_BY_PRINCIPAL_QUERY = "SELECT authUsr FROM AuthenticatedUser AS authUsr WHERE authUsr.principalName = :pname";

    final static String GET_NUM_WITH_PRINCIPAL_QUERY = "SELECT count(au) FROM AuthenticatedUser as au WHERE au.principalName = :pname";

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    PasswordHash hasher;

    @Inject
    JsonWebToken webToken;

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
     * Returns the user with the provided user principal
     *
     * @param principal the principal to search for
     *
     * @return the user if found null if not.
     */
    public AuthenticatedUser getUserFromPrincipal(String principal) {
        TypedQuery<AuthenticatedUser> query = entityManager.createQuery(GET_USER_BY_PRINCIPAL_QUERY,
                                                                        AuthenticatedUser.class
        );
        query.setParameter("pname", principal);
        try {
            return query.getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }

    }

    /**
     * Returns the user with the provided user id.
     *
     * @param userId the id of the user to find
     *
     * @return the user with the provided id null if none are found
     */
    public AuthenticatedUser getUserFromId(long userId) {
        AuthenticatedUser authenticatedUser = entityManager.find(AuthenticatedUser.class, userId);
        return authenticatedUser;
    }

    /**
     * Util method, gets the current {@link CredentialValidationResult}
     *
     * @param userId   Users id
     * @param password Users password
     *
     * @return the credential val result for this username pass combo
     */
    public CredentialValidationResult getValidationResult(long userId, String password) {
        return identityStoreHandler.validate(new UsernamePasswordCredential(String.valueOf(userId), password));
    }

    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public AuthenticatedUser getCurrentAuthUser() {
        return getUserFromId(Long.parseLong(webToken.getSubject()));
    }


    /**
     * Cheks if the provided principal is currently in use
     *
     * @param principal the principal to chek
     *
     * @return true if the principal is used false if not
     */
    public boolean isPrincipalInUse(String principal) {
        Query query = entityManager.createQuery(GET_NUM_WITH_PRINCIPAL_QUERY);
        query.setParameter("pname", principal);
        try {
            long numWith = (long) query.getSingleResult();
            System.out.println(numWith);
            if (numWith == 0) {
                return false;
            }
        } catch (NoResultException ignore) {
        }
        return true;


    }

    /**
     * Changes the password for the current user if the old one is valid
     * todo: en hvis passordet er glemt?
     *
     * @param newPass the new password
     * @param oldPass the old password
     *
     * @return true if password is changed false if not
     */
    public boolean changePassword(String newPass, String oldPass) {
        boolean suc                        = false;
        var     credentialValidationResult = getValidationResult(Long.parseLong(webToken.getSubject()), oldPass);
        if (isAuthValid(credentialValidationResult)) {
            AuthenticatedUser user = getCurrentAuthUser();
            user.setPassword(hasher.generate(newPass.toCharArray()));
            entityManager.merge(user);
            suc = true;
        }
        return suc;

    }

}
