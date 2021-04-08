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
import java.util.List;
import java.util.Optional;


@Transactional
@RequestScoped
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
    KeyService keyService;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    /**
     * Util method checks if the auth {@link CredentialValidationResult} result is valid
     *
     * @param result
     * @return
     */
    public boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }


    public boolean isAuthValid(String username,String password) {
        var result = identityStoreHandler.validate(new UsernamePasswordCredential(username, password));
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




    public String getToken(UsernamePasswordData usernamePasswordData) {
        String token = null;

        AuthenticatedUser user = this.getUserFromPrincipal(usernamePasswordData.getUserName());
        System.out.println(user.getId());
        if (user != null) {
            var validationResult = getValidationResult(String.valueOf(user.getId()),usernamePasswordData.getPassword());
            System.out.println(validationResult.getStatus());
            System.out.println(isAuthValid(validationResult));
            if (isAuthValid(validationResult)) {
                token = keyService.generateNewJwtToken(usernamePasswordData.getUserName(),
                                                       user.getId(),
                                                       validationResult.getCallerGroups());
            }
        }


        return token;


    }

    /**
     * Returns the user with the provided user id.
     *
     * @param userId the id of the user to find
     * @return the user with the provided id null if none are found
     */
    public AuthenticatedUser getUserFromId(long userId) {
        AuthenticatedUser authenticatedUser = entityManager.find(AuthenticatedUser.class, userId);
        return authenticatedUser;
    }


    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public AuthenticatedUser getCurrentAuthUser() {
        var maybeSubject = jwtSubject.get();
        //todo:handle pot error
        return maybeSubject.map(s -> getUserFromId(Long.parseLong(s))).orElse(null);
    }


    /**
     * Cheks if the provided principal is currently in use
     *
     * @param principal the principal to chek
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

    public AuthenticatedUser createUser(NewAuthUserData newAuthUserData) {
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
            return user;
        } else {
            return null;
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
        boolean suc                        = false;
        var maybeSubject = jwtSubject.get();
        if (maybeSubject.isPresent()){
            if (isAuthValid(maybeSubject.get(), oldPass)) {
                AuthenticatedUser user = getCurrentAuthUser();
                user.setPassword(hasher.generate(newPass.toCharArray()));
                entityManager.merge(user);
                suc = true;
            }
        }

        return suc;

    }

}
