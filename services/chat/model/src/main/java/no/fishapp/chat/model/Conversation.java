package no.fishapp.chat.model;


import lombok.*;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The entity for a Conversation. Stored in the SQL database.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "conversations")
public class Conversation {

    /**
     * Holds supported states the conversation may be in.
     */
    public enum State {
        ACTIVE,
        COMPLETED
    }

    /**
     * The identifier of the conversation.
     * Used as PK in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    long id;


    /**
     * The ID of the last message.
     * Used by clients to identify
     * the last message sent
     */
    @Getter
    long lastMessageId = -1;

    /**
     * A list of the meessages
     * related to the conversation
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    List<Message> messages;

    /**
     * The current state of the
     * conversation
     */
    @Enumerated(EnumType.STRING)
    State state = State.ACTIVE;

    /**
     * The listing ID related to this
     * conversation
     */
    long listingId;

    /**
     * The user ID of the creator
     * of the listing (far-end User)
     */
    long listingCreatorUserId;


    /**
     * The user ID of the initiating
     * user for this conversation (near-end User)
     */
    long conversationStarterUserId;


    /**
     * A timestamp in UNIX Epoch when
     * the conversation was created
     */
    @Getter
    @Column(name = "created_date")
    Long createdDate;

    /**
     * Retrieves the system time, saves it
     * as a timestamp before writing the
     * conversation to the database.
     */
    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }


    /**
     * Adds a message to the conversation
     *
     * @param message The {@link Message} to add
     */
    public void addMessage(Message message) {
        this.lastMessageId = message.getId();
        this.messages.add(message);
    }

    /**
     * Outputs the list of messages
     * related to this conversation
     *
     * @return List holding {@link Message} objects
     */
    public List<Message> getMessages() {
        if (messages == null) {
            return new ArrayList<>();
        }
        return messages;
    }

    /**
     * Return the last {@link Message} inside an {@code Optional}
     * from this conversation. If no messages exist we return
     * an empty {@code Optional}
     *
     * @return The {@code Optional} with an {@link Message}.
     */
    public Optional<Message> getLastMessage() {
        if (this.messages.size() > 0) {
            return Optional.of(messages.get(messages.size() - 1));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Used as a security measure to verify if the {@code User} asking
     * for the information is related to this conversation.
     *
     * @param id The ID of the {@code User}.
     * @return A {@code Boolean} that the {@code User} is part of the conversation.
     */
    public boolean isUserInConv(long id) {
        return id == conversationStarterUserId || id == listingCreatorUserId;
    }

}
