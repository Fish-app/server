package no.fishapp.checkout.model;


import lombok.Data;
import no.fishapp.checkout.model.dibsapi.DibsOrder;
import no.fishapp.checkout.model.enums.SubscriptionStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class SubscribedUser {

    @Id
    @NotNull
    @Column(unique = true, nullable = false)
    private long userid;

    private String subscriptionId;

    @OneToMany
    private List<DibsOrder> subscriptionDibsOrders;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    private boolean willRenew;

    public void addOrder(DibsOrder dibsOrder) {
        dibsOrder.setOrderOwner(this);
        if (this.subscriptionDibsOrders == null) {
            this.subscriptionDibsOrders = new ArrayList<>();
        }
        this.subscriptionDibsOrders.add(dibsOrder);

    }


}
