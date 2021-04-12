package no.fishapp.chat.model;


import lombok.*;


import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@Table(name = "conversations")
public class Conversation {

    public enum State {
        ACTIVE, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    long id;


    @Getter
    long lastMessageId = -1;

    @OneToMany(cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JsonbTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    List<Message> messages;

    @Enumerated(EnumType.STRING)
    State state = State.ACTIVE;

    long listingId;

    long listingCreatorUserId;


    long conversationStarterUserId;

    @Getter
    @Column(name = "created_date")
    Long createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }


    public void addMessage(Message message) {
        this.lastMessageId = message.getId();
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        if (messages == null) {
            return new ArrayList<>();
        }
        return messages;
    }

    public Optional<Message> getFirstMessage() {
        if (this.messages.size() > 0) {
            return Optional.of(messages.get(0));
        } else {
            return Optional.empty();
        }
    }
}
