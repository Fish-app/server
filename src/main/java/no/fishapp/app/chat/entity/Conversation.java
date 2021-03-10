package no.fishapp.app.chat.entity;


import lombok.*;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
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

    // IDs for first and last msg;

    @Getter
    long lastMessageId = - 1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    List<Message> messages;

    @Enumerated(EnumType.STRING)
    State state = State.ACTIVE;

    @ManyToOne(cascade = CascadeType.ALL)
    Listing conversationListing;

    long listingCreatorUserId;

    @Getter
    User conversationStarterUser;

    @Getter
    @Column(name = "created_date")
    Long createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }

    public Conversation(Listing conversationListing, User currentUser) {
        this.conversationListing     = conversationListing;
        this.listingCreatorUserId    = conversationListing.getCreator().getId();
        this.conversationStarterUser = currentUser;
        this.messages                = new ArrayList<>();
    }


    public boolean isUserInConversation(User user) {
        return listingCreatorUserId == user.getId() || conversationStarterUser.getId() == user.getId();
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

    public Listing getConversationListing() {
        return conversationListing;
    }
}
