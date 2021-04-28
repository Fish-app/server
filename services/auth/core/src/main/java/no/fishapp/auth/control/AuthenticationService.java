package no.fishapp.auth.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import java.util.Optional;


@Transactional
@RequestScoped
@Log
public class AuthenticationService {

    /**
     * Returns the user with the principal name equal to the param 'pname'
     */
    static final String GET_USER_BY_PRINCIPAL_QUERY = "SELECT authUsr FROM AuthenticatedUser AS authUsr WHERE authUsr.principalName = :pname";

    /**
     * Returns the number of users with the principal name equal to the param 'pname'
     * This allows for checking if a name is free without having to return the POJO
     */
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
     * Util method checks if the auth {@link CredentialValidationResult} result is valid.
     *
     * @param result the result from a credential validation
     * @return {@code true} if the {@code CredentialValidationResult} is valid, {@code false} if not
     */
    protected boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    /**
     * Checks if the provided {@code userId} and  {@code password} combination is a valid login.
     *
     * @param userId   the users id
     * @param password the users password
     * @return {@code true} if the login is valid, {@code false} if not
     */
    protected boolean isAuthValid(String userId, String password) {
        var result = identityStoreHandler.validate(new UsernamePasswordCredential(userId, password));
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    /**
     * Util method, gets the current {@link CredentialValidationResult}.
     *
     * @param userId   Users id
     * @param password Users password
     * @return the {@code CredentialValidationResult} result for this username password combination.
     */
    protected CredentialValidationResult getValidationResult(String userId, String password) {
        return identityStoreHandler.validate(new UsernamePasswordCredential(userId, password));
    }


    /**
     * Returns an {@link Optional} with the user with the provided user {@code principal} if present.
     * if not an empty {@code Optional} instance is returned.
     *
     * @param principal the principal to search for
     * @return optional containing the user.
     */
    public Optional<AuthenticatedUser> getUserFromPrincipal(String principal) {
        TypedQuery<AuthenticatedUser> query = entityManager
                .createQuery(GET_USER_BY_PRINCIPAL_QUERY, AuthenticatedUser.class);
        query.setParameter("pname", principal);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ignore) {
            return Optional.empty();
        }

    }


    /**
     * Tries to log inn with the provided {@code usernamePasswordData}. If the login is sucsessfull
     * a {@link Optional} with the login jwt is returned.
     * If the login fails an empty {@code Optional} instance is returned
     *
     * @param usernamePasswordData the {@link UsernamePasswordData} containing the login data
     * @return a {@link Optional} with the login token.
     */
    public Optional<String> getToken(UsernamePasswordData usernamePasswordData) {

        Optional<AuthenticatedUser> userOptional = getUserFromPrincipal(usernamePasswordData.getUserName());
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
     * Returns an {@link Optional} containing the {@link AuthenticatedUser} with the provided user id.
     * If no {@code AuthenticatedUser} with the provided id id found a empty {@code optional} is returned.
     *
     * @param userId the id of the user to find
     * @return an {@code Optional} containing the {@code AuthenticatedUser} with the provided id.
     */
    public Optional<AuthenticatedUser> getUserFromId(long userId) {
        return Optional.ofNullable(entityManager.find(AuthenticatedUser.class, userId));
    }


    /**
     * Returns an {@link Optional} containing the currently logged in {@link AuthenticatedUser}.
     * If there is an error fetching the current {@code AuthenticatedUser} empty {@code Optional} is returned.
     *
     * @return An {@code Optional} the currently logged in {@code AuthenticatedUser}.
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
     * Checks if the provided {@code principal} is in use.
     *
     * @param principal the principal to check
     * @return {@code true} if the principal is used {@code false} if not.
     */
    public boolean isPrincipalInUse(String principal) {
        TypedQuery<Long> query = entityManager.createQuery(GET_NUM_WITH_PRINCIPAL_QUERY, Long.class);
        query.setParameter("pname", principal);
        try {
            long numWith = query.getSingleResult();
            if (numWith == 0) {
                return false;
            }
        } catch (NoResultException ignore) {
        }
        return true;

    }

    /**
     * Creates a new {@link AuthenticatedUser} with the provided {@link NewAuthUserData}
     * and returns an {@link Optional} containing the resulting {@code AuthenticatedUser}.
     * If any of the {@link NewAuthUserData} group names ar not in the names defined in {@link Group}
     * the request is asumed to be erroneous and an empty {@code Optional} is returned.
     *
     * @param newAuthUserData the {@code NewAuthUserData} containing the new user data.
     * @return An {@code Optional} the new {@code AuthenticatedUser} if sucsessfull or {@code empty} if not.
     */
    public Optional<AuthenticatedUser> createUser(NewAuthUserData newAuthUserData) {
        if (!isPrincipalInUse(newAuthUserData.getUserName()) && !newAuthUserData.getGroups().isEmpty()) {
            AuthenticatedUser user = new AuthenticatedUser(hasher.generate(newAuthUserData.getPassword().toCharArray()),
                                                           newAuthUserData.getUserName());
            if (newAuthUserData.getGroups().stream().noneMatch(Group::isValidGroupName)) {
                log.severe("Invalig groop names recived");
                return Optional.empty();
            }
            newAuthUserData.getGroups().forEach(groupName -> {
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
     * Changes the password for the current {@link AuthenticatedUser} if the {@code oldPass} is valid.
     *
     * @param newPass the new password
     * @param oldPass the old password
     * @return {@code true} if password is changed {@code false} if not
     */
    public boolean changePassword(String newPass, String oldPass) {
        boolean                     suc          = false;
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


    /**
     * For testing.
     */
    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * For testing.
     */
    protected void setJwtSubject(Instance<Optional<String>> jwtSubject) {
        this.jwtSubject = jwtSubject;
    }


}
