package no.***REMOVED***.app.conversation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.user.entity.User;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    // TODO: May add images to the messages

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @Column(columnDefinition = "TEXT")
    String content;

    @ManyToOne
    User sender;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
    }
}
