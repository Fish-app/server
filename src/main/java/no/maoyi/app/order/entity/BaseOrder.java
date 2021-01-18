package no.***REMOVED***.app.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.conversation.entity.Conversation;
import no.***REMOVED***.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@Entity
public abstract class BaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    @ManyToOne(cascade = CascadeType.ALL)
    User creator;

    Boolean isOrderOpen;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }


}
