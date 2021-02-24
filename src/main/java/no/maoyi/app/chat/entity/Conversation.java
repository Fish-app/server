package no.***REMOVED***.app.chat.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.***REMOVED***.app.listing.entity.Listing;
import no.***REMOVED***.app.user.entity.User;

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


    // The users in this conversation; used to determine what users
    // that need to get notified when a new message is added
    //N-M REF
    @ManyToMany(mappedBy = "userConversations")
    @JsonbTransient
    List<User> participants;

    long listingUserId;
    long initUserId;

    public Conversation(Listing conversationListing, User currentUser) {
        this.conversationListing = conversationListing;
        this.listingUserId = conversationListing.getCreator().getId();
        this.initUserId = currentUser.getId();
    }

    public boolean isUserInConversation(User user) {
        return listingUserId == user.getId() || initUserId == user.getId();
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

    public List<User> getParticipants() {
        if (participants == null) {
            return new ArrayList<>();
        }
        return participants;
    }
}
