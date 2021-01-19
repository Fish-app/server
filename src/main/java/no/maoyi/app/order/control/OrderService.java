package no.maoyi.app.order.control;

import no.maoyi.app.order.entity.BuyBaseOrder;
import no.maoyi.app.user.boundry.UserResource;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OrderService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken token;

    @Inject
    UserResource userResource;

    public BuyBaseOrder newBuyOrder() {
        // logict to create and presist comes here
        return new BuyBaseOrder();
    }
}
