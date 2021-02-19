package no.***REMOVED***.app.chat.control;

import no.***REMOVED***.app.chat.entity.Conversation;
import no.***REMOVED***.app.chat.entity.Message;
import no.***REMOVED***.app.listing.entity.Listing;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

public class ChatService {

    @PersistenceContext
    EntityManager em;


    public Conversation newConversation() {
        Conversation input = new Conversation();
        if (addToDB(input)) {
            return input;
        } else {
            return null;
        }
    }

    public Conversation findConversationById (long id) {
        return em.find(Conversation.class, id);
    }

    public Conversation attachConversation(Conversation conversation, long listingId) {
        Listing baseOrder =  em.find(Listing.class, listingId);
        if ( baseOrder == null | conversation == null) return null;
        conversation.setBaseOrder(baseOrder);
        return updateToDB(conversation);

    }

    Message addMessage(Message message, Conversation conversation) {
        // Find the conversation by id
        // If found, add a message to the List inside the conversation
        Conversation foundConversation = getFromDB(conversation);
        if (foundConversation == null || message == null) return null;
        List<Message> msgList = getMsgs(foundConversation);
        msgList.add(message);
        em.persist(message);
        foundConversation = updateToDB(foundConversation);
        if(foundConversation == null) {
            return null;
        } else {
            return message;
        }
    }


    private boolean addToDB(Conversation input) {
        try {
            em.persist(input);
            return true;
        } catch (PersistenceException pe) {
            return false;
        }
    }

    private Conversation updateToDB(Conversation input) {
        try {
            Conversation output = em.merge(input);
            return output;
        } catch (PersistenceException pe) {
            return null;
        }
    }

    private Conversation getFromDB(Conversation input) {
        try {
           return em.find(Conversation.class, input);
        } catch (PersistenceException pe) {
            return null;
        }
    }


    private List<Message> getMsgs(Conversation conversation) {
        Conversation result = em.find(Conversation.class, conversation);
        if (result == null) return null;
        List<Message> messageList = result.getMessages();
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }


}
