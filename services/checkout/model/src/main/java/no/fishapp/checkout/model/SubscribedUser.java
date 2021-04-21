package no.fishapp.checkout.model;


import lombok.Data;
import no.fishapp.checkout.model.enums.SubscriptionStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class SubscribedUser {

    @Id
    @NotNull
    @Column(unique = true, nullable = false)
    private long userid;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

}
