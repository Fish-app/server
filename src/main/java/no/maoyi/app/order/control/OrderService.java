package no.maoyi.app.order.control;

import no.maoyi.app.order.entity.Order;
import no.maoyi.app.user.boundry.AuthenticationService;
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
    AuthenticationService authenticationService;

    public Order newOrder(){
        // logict to create and presist comes here
        return new Order();
    }
}
