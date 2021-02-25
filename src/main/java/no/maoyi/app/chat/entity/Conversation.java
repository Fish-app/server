package no.maoyi.app.chat.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.user.entity.User;

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


    @Getter
    long lastMessageId;

    @Getter
    long lastSenderId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    List<Message> messages;

//    @Column(columnDefinition = "TEXT")
//    String title;
//
//    @Column(columnDefinition = "TEXT")
//    String description;

    @Enumerated(EnumType.STRING)
    State state = State.ACTIVE;

    // N-1 Owner
    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "base_order_id", referencedColumnName = "id")
            Listing conversationListing;



    long listingCreatorUserId;
    long conversationStarterUserId;

    public Conversation(Listing conversationListing, User currentUser) {
        this.conversationListing = conversationListing;
        this.listingCreatorUserId = conversationListing.getCreator().getId();
        this.conversationStarterUserId = currentUser.getId();
    }

    public boolean isUserInConversation(User user) {
        return listingCreatorUserId == user.getId() || conversationStarterUserId == user.getId();
    }

    public void addMessage(Message message) {
        this.lastMessageId = message.getId();
        this.lastSenderId = message.getSender().getId();
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
