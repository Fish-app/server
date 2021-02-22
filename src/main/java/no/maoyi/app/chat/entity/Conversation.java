package no.maoyi.app.chat.entity;


import lombok.*;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {

    public enum State {
        ACTIVE, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //List<Message> messages;

    @Column(columnDefinition = "TEXT")
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    State state = State.ACTIVE;

    // N-1 Owner
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_order_id", referencedColumnName = "id")
    Listing baseOrder;

    // N-1 REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "conversation")
    //@JsonbTransient
    List<Message> messages;

    // The users in this conversation; used to determine what users
    // that need to get notified when a new message is added
    //N-M REF
    @ManyToMany(mappedBy = "userConversations")
    @JsonbTransient
    List<User> participants;
}
