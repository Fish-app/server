package no.***REMOVED***.app.chat.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.***REMOVED***.app.listing.entity.Listing;
import no.***REMOVED***.app.user.entity.User;

import javax.json.bind.annotation.JsonbAnnotation;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.ArrayList;
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
    long firstMessageId;
    @Getter
    long lastMessageId;

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
    long conversationStarterUserId;

    public Conversation(Listing conversationListing, User currentUser) {
        this.conversationListing = conversationListing;
        this.listingCreatorUserId = conversationListing.getCreator().getId();
        this.conversationStarterUserId = currentUser.getId();
        this.messages = new ArrayList<>();
    }

    public boolean isUserInConversation(User user) {
        return listingCreatorUserId == user.getId() || conversationStarterUserId == user.getId();
    }

    public void addMessage(Message message) {
        this.lastMessageId = message.getId();
        this.messages.add(message);
        if(this.messages.indexOf(message) == 0) {
            // Save the first message id (used by client to determine limit, as msg ID's are global)
            this.firstMessageId = message.getId();
        }
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
