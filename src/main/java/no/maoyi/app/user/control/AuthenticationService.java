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
    KeyService keyService;

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken webToken;

    @Inject
    PasswordHash hasher;


    private static String GET_USER_FROM_EMAIL = "SELECT u FROM User as u WHERE u.email like :mail";
    private static String GET_SELLER_FROM_EMAIL = "SELECT u FROM Seller as u WHERE u.email like :mail";


    public User getUser(BigInteger userId) throws NoResultException {
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public User getLoggedInUser() {
        return getUser(BigInteger.valueOf(Long.parseLong(webToken.getSubject())));
    }

    public User getUserFromEmail(String email) {

        TypedQuery<User> query = entityManager.createQuery(GET_USER_FROM_EMAIL, User.class);
        query.setParameter("mail", "%" + email + "%");
        try {
            return query.getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }
    }

    public Seller getSellerFromEmail(String email) {

        TypedQuery<Seller> query = entityManager.createQuery(GET_SELLER_FROM_EMAIL, Seller.class);

        query.setParameter("mail", "%" + email + "%");
        try {
            return query.getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }
    }


    // -- auth stuff -- //
    public boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    public CredentialValidationResult gerValidationResult(BigInteger userId, String password) {
        System.out.println(userId);
        System.out.println(password);
        return identityStoreHandler.validate(new UsernamePasswordCredential(String.valueOf(userId), password));
    }


    public User createUser(String name, String username, String email, String password) {

        User  newUser   = new User(email, name, username, password);
        Group usergroup = entityManager.find(Group.class, Group.USER_GROUP_NAME);
        newUser.setPassword(hasher.generate(password.toCharArray()));
        newUser.getGroups().add(usergroup);

        entityManager.persist(newUser);

        return newUser;

    }

    public Seller createSeller(String name, String username, String email, String password, String bankAccountNumber, String regNumber) {
        Seller newSeller = new Seller(name, username, email, password, bankAccountNumber, regNumber);
        newSeller.getGroups().add(entityManager.find(Group.class, Group.USER_GROUP_NAME));
        newSeller.getGroups().add(entityManager.find(Group.class, Group.SELLER_GROUP_NAME));
        newSeller.setPassword(hasher.generate(password.toCharArray()));

        entityManager.persist(newSeller);

        return newSeller;
    }

}
