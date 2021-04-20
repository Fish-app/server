package no.fishapp.auth.control;

import io.jsonwebtoken.Claims;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.*;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import java.util.Optional;


@Transactional
@RequestScoped
public class AuthenticationService {

    static final String GET_USER_BY_PRINCIPAL_QUERY = "SELECT authUsr FROM AuthenticatedUser AS authUsr WHERE authUsr.principalName = :pname";

    static final String GET_NUM_WITH_PRINCIPAL_QUERY = "SELECT count(au) FROM AuthenticatedUser as au WHERE au.principalName = :pname";


    @Inject
    IdentityStoreHandler identityStoreHandler;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    PasswordHash hasher;

    @Inject
    KeyService keyService;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    /**
     * Util method checks if the auth {@link CredentialValidationResult} result is valid
     *
     * @param result the result from a credential validation
     * @return true if the status on the result is valid, false if not
     */
    public boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }


    public boolean isAuthValid(String userId, String password) {
        var result = identityStoreHandler.validate(new UsernamePasswordCredential(userId, password));
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    /**
     * Util method, gets the current {@link CredentialValidationResult}
     *
     * @param userId   Users id
     * @param password Users password
     * @return the credential val result for this username pass combo
     */
    public CredentialValidationResult getValidationResult(String userId, String password) {
        return identityStoreHandler.validate(new UsernamePasswordCredential(userId, password));
    }


    /**
     * Returns the user with the provided user principal
     *
     * @param principal the principal to search for
     * @return the user if found null if not.
     */
    public Optional<AuthenticatedUser> getUserFromPrincipal(String principal) {
        TypedQuery<AuthenticatedUser> query = entityManager.createQuery(GET_USER_BY_PRINCIPAL_QUERY,
                                                                        AuthenticatedUser.class
        );
        query.setParameter("pname", principal);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ignore) {
            return Optional.empty();
        }

    }


    public Optional<String> getToken(UsernamePasswordData usernamePasswordData) {

        Optional<AuthenticatedUser> userOptional = this.getUserFromPrincipal(usernamePasswordData.getUserName());
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            var validationResult = getValidationResult(String.valueOf(user.getId()),
                                                       usernamePasswordData.getPassword());
            if (isAuthValid(validationResult)) {
                return Optional.of(keyService.generateNewJwtToken(usernamePasswordData.getUserName(),
                                                                  user.getId(),
                                                                  validationResult.getCallerGroups()));
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the user with the provided user id.
     *
     * @param userId the id of the user to find
     * @return the user with the provided id null if none are found
     */
    public Optional<AuthenticatedUser> getUserFromId(long userId) {
        return Optional.ofNullable(entityManager.find(AuthenticatedUser.class, userId));
    }


    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public Optional<AuthenticatedUser> getCurrentAuthUser() {
        if (jwtSubject.get().isPresent()) {
            long userId = Long.parseLong(jwtSubject.get().get());
            return getUserFromId(userId);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Checks if the provided principal is currently in use
     *
     * @param principal the principal to chek
     * @return true if the principal is used false if not
     */
    public boolean isPrincipalInUse(String principal) {
        Query query = entityManager.createQuery(GET_NUM_WITH_PRINCIPAL_QUERY);
        query.setParameter("pname", principal);
        try {
            long numWith = (long) query.getSingleResult();
            if (numWith == 0) {
                return false;
            }
        } catch (NoResultException ignore) {
        }
        return true;

    }

    public Optional<AuthenticatedUser> createUser(NewAuthUserData newAuthUserData) {
        if (!isPrincipalInUse(newAuthUserData.getUserName())) {
            AuthenticatedUser user = new AuthenticatedUser(hasher.generate(newAuthUserData.getPassword()
                                                                                          .toCharArray()),
                                                           newAuthUserData.getUserName()
            );
            newAuthUserData.getGroups().stream().filter(Group::isValidGroupName).forEach(groupName -> {
                Group dbGroup = entityManager.find(Group.class, groupName);
                if (dbGroup != null) {
                    user.getGroups().add(dbGroup);
                }
            });

            entityManager.persist(user);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }


    }

    /**
     * Changes the password for the current user if the old one is valid
     * todo: en hvis passordet er glemt?
     *
     * @param newPass the new password
     * @param oldPass the old password
     * @return true if password is changed false if not
     */
    public boolean changePassword(String newPass, String oldPass) {
        boolean                     suc          = false;
        var                         maybeSubject = jwtSubject.get();
        Optional<AuthenticatedUser> userOptional = getCurrentAuthUser();
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            if (isAuthValid(String.valueOf(user.getId()), oldPass)) {
                user.setPassword(hasher.generate(newPass.toCharArray()));
                entityManager.merge(user);
                suc = true;
            }
        }

        return suc;

    }

}
