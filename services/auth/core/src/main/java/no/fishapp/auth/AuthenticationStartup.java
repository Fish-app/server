package no.fishapp.auth;


import no.fishapp.auth.control.AuthenticationService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.Group;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * Manages the startup actions for the Authentication service.
 * This manly includes generating the different admin and management user needed for the cluser to work
 */
@Singleton
@Startup
public class AuthenticationStartup {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    AuthenticationService authenticationService;

    /**
     * The inter container communication username
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "container_default")
    private String username;

    /**
     * The inter container communication password
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;

    /**
     * The admin user username
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.adminUsername", defaultValue = "admin_default")
    private String adminUsername;

    /**
     * The admin user password
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.adminPassword", defaultValue = "fishapp")
    private String adminPassword;


    /**
     * The dibs api webhook callback username
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.dibsapi.username", defaultValue = "webhook_default")
    private String dibsApiUserUsername;

    /**
     * The dibs api webhook callback password
     */
    @Inject
    @ConfigProperty(name = "fishapp.service.dibsapi.password", defaultValue = "fishapp")
    private String dibsApiUserPassword;

    /**
     * starts the async tasks generate the users and groups.
     */
    @PostConstruct
    @Asynchronous
    public void initialize() {
        this.persistUserGroups();
        this.createIfAbsent(username, password, List.of(Group.CONTAINER_GROUP_NAME));
        this.createIfAbsent(adminUsername, adminPassword, List.of(Group.ADMIN_GROUP_NAME));
        this.createIfAbsent(dibsApiUserUsername, dibsApiUserPassword, List.of(Group.API_CALLBACK_GROUP_NAME));


    }

    /**
     * Validates that the database contains all the {@link Group} names. If not, any missing name is added.
     */
    public void persistUserGroups() {
        long groups = (long) entityManager.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if (groups != Group.GROUPS.length) {
            for (String group : Group.GROUPS) {
                entityManager.persist(new Group(group));
            }
        }
    }

    /**
     * Checks if the {@link AuthenticatedUser} used to for inter container communication in the cluster exists,
     * and creates it if it does not
     */
    public void createContainerJwtUser() {
        Optional<AuthenticatedUser> user = authenticationService.getUserFromPrincipal(username);

        if (user.isEmpty()) {
            var newAuthUserData = new NewAuthUserData();
            newAuthUserData.setUserName(username);
            newAuthUserData.setPassword(password);
            newAuthUserData.setGroups(List.of(Group.CONTAINER_GROUP_NAME));
            var authUser = authenticationService.createUser(newAuthUserData);
            //todo: chek if this has sucseded
        }
    }

    /**
     * Checks if the admin {@link AuthenticatedUser} exists,
     * and creates it if it does not
     */
    public void createAdminUser() {
        Optional<AuthenticatedUser> user = authenticationService.getUserFromPrincipal(adminUsername);

        if (user.isEmpty()) {
            var newAuthUserData = new NewAuthUserData();
            newAuthUserData.setUserName(adminUsername);
            newAuthUserData.setPassword(adminPassword);
            newAuthUserData.setGroups(List.of(Group.ADMIN_GROUP_NAME));
            var authUser = authenticationService.createUser(newAuthUserData);
            //todo: chek if this has sucseded
        }
    }

    public void createIfAbsent(String username, String password, List<String> groops) {
        Optional<AuthenticatedUser> user = authenticationService.getUserFromPrincipal(username);

        if (user.isEmpty()) {
            var newAuthUserData = new NewAuthUserData();
            newAuthUserData.setUserName(username);
            newAuthUserData.setPassword(password);
            newAuthUserData.setGroups(groops);
            var authUser = authenticationService.createUser(newAuthUserData);
            //todo: chek if this has sucseded
        }
    }


}