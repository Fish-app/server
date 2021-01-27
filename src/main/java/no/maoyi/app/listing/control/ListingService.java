package no.maoyi.app.listing.control;

import no.maoyi.app.user.boundry.UserResource;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ListingService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken token;

    @Inject
    UserResource userResource;

    public void newBuyOrder() {
        // logict to create and presist comes here

    }
}
