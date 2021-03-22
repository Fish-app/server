package no.fishapp.auth;

import no.fishapp.auth.entity.Group;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Startup actions for authentication that are executed on server start.
 */
@Singleton
@Startup
public class AuthenticationStartup {
    @PersistenceContext
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        persistUserGroups();
    }

    /**
     * Makes sure that our user grous are added to the database.
     */
    public void persistUserGroups() {
        long groups = (long) entityManager.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if (groups != Group.GROUPS.length) {
            for (String group : Group.GROUPS) {
                entityManager.merge(new Group(group));
            }
        }
    }
}