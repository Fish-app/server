package no.maoyi.app.conversation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.user.entity.User;

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
    long id;

    @Column(columnDefinition = "TEXT")
    String content;

    @ManyToOne
    User sender;

    @Temporal(javax.persistence.TemporalType.DATE)
    Long createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }
}
