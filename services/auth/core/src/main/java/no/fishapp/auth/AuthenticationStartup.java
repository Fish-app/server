package no.fishapp.auth;


import no.fishapp.auth.control.AuthenticationService;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.Group;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Startup actions for authentication that are executed on server start.
 */
@Singleton
@Startup
public class AuthenticationStartup {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "fishapp")
    private String username;

    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;

    @PostConstruct
    public void initialize(){
        this.persistUserGroups();
        this.createContainerJwtUser();

    }

    /**
     * Makes sure that our user grous are added to the database.
     */
    public void persistUserGroups() {
        long groups = (long) entityManager.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if (groups != Group.GROUPS.length) {
            for (String group : Group.GROUPS) {
                entityManager.persist(new Group(group));
            }
        }
    }

    public void createContainerJwtUser(){
        AuthenticatedUser user = authenticationService.getUserFromPrincipal(username);

        if (user == null){
            var newAuthUserData = new NewAuthUserData();
            newAuthUserData.setUserName(username);
            newAuthUserData.setPassword(password);
            newAuthUserData.setGroups(List.of(Group.CONTAINER_GROUP_NAME));
            var authUser = authenticationService.createUser(newAuthUserData);
            //todo: chek if this has sucseded
        }
    }
}