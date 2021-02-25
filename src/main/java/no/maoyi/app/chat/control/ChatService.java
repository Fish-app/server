package no.maoyi.app.chat.control;

import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.Message;
import no.maoyi.app.listing.control.ListingService;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.user.control.UserService;
import no.maoyi.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class ChatService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ListingService listingService;

    @Inject
    UserService userService;

    /**
     *  future: handle pictures somehow
     *  future: handle push notfications outside of app
     */

    /**
     * creates a new conversation between the current user and the owner of the listing with the provided id
     *
     * @param listingId the listing to hav conversation about
     * @return the conversation object if ok null if not
     */
    public Conversation newListingConversation(long listingId) {

        try {
            Listing listing = entityManager.find(Listing.class, listingId);
            if (listing == null) return null; // Avoid nullptr below
            User listingOwner = listing.getCreator();
            User conversationStarter = userService.getLoggedInUser();
            Conversation conversation = new Conversation(listing, conversationStarter);
            entityManager.persist(conversation);
            entityManager.flush();
            entityManager.refresh(conversation);

            if(userService.addConversationToUser(conversation, listingOwner) == false) throw new PersistenceException();
            if(userService.addConversationToUser(conversation, conversationStarter) == false) throw new PersistenceException();
            return conversation;
        } catch (PersistenceException pe) {
            return null;
        }
    }

    /**
     * returns the conversatin with the provided id
     *
     * @param convId the conversations id
     * @return the conversation, null if not found
     */
    public Conversation getConversation(long convId) {
        return entityManager.find(Conversation.class, convId);
    }

    /**
     * Registers a message body as sent from the current logged in user in a conversation.
     * whether or not the user is allowed is not cheked
     *
     * @param messageBody  the string content of the message
     * @param conversation the conversation objet for the conversation
     */
    public Conversation sendMessage(String messageBody, Conversation conversation) {
        Message message = new Message(messageBody, userService.getLoggedInUser());
        try {
            entityManager.persist(message);
            entityManager.flush();
            conversation.addMessage(message);
            entityManager.persist(conversation);
            entityManager.flush();
            return conversation;
        } catch (Exception e) {
            System.out.println("CONTROL-CHAT: Persistence failure sendMsg");
            return null;
        }


    }

    /**
     * Returns all messages newer than the message with the provided id.
     *
     * @param convId        the id of the concversation wo chek
     * @param fromMessageId (exclusive) return messages newer than the message with this id
     * @return a list with the messages
     */
    public List<Message> getMessagesTo(long convId, long fromMessageId) {
        Conversation conversation = getConversation(convId);
        return conversation.getMessages()
                           .stream()
                           .filter(message -> message.getId() > fromMessageId)
                           .collect(
                                   Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a range of messages from the message with the provided id to the message with the offset
     *
     * @param convId the id of the conversation
     * @param fromId the id to collect offset from
     * @param offset the offset possetive or negative
     * @return a list of messages
     */
    public List<Message> getMessageRange(Long convId, Long fromId, Long offset) {
        Conversation  conversation = getConversation(convId);
        List<Message> messages     = conversation.getMessages();
        if(messages == null) return new ArrayList<>();
        Message anchor       = entityManager.find(Message.class, fromId);
        int     elementIndex = messages.indexOf(anchor);

        if (offset == null) offset = 0L; // default value if null
        if (offset > 0) {
            return messages.subList(elementIndex, (int) Math.min(messages.size(), offset));
        } else {
            return messages.subList((int) Math.min(elementIndex, Math.abs(offset)), elementIndex);
        }
    }
}