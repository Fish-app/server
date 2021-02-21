package no.***REMOVED***.app.chat.control;

import no.***REMOVED***.app.chat.entity.Conversation;
import no.***REMOVED***.app.chat.entity.Message;
import no.***REMOVED***.app.listing.control.ListingService;
import no.***REMOVED***.app.listing.entity.Listing;
import no.***REMOVED***.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class ChatService {

    @PersistenceContext
    EntityManager em;

    @Inject
    ListingService ls;


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

    // TODO: Utsatt til listing fungerer og kan brukest som testing
    public Conversation findConversationByBuyerAndSeller(Long listingId, User buyer) {
        Listing listing = ls.findListingById(listingId);
        if (listing == null || buyer == null) return null;
        // find the unique conversation between logged on buyer and seller of a specific listing
        // find out how to find the seller of a listing:
        // in offerlisting we are good, as seller == creator
        // in buyrequest TODO: find out how/what the seller is
        return null;
    }

    // TODO: Utsatt til listing fungerer
    boolean doesConversationExsistBetweenBuyerAndSeller(Listing listing, User buyer) {
       if (listing == null || buyer == null) return false;
       return  false;
    }

    public Conversation addConversationToListing(Conversation conversation, long listingId) {
        // Find a listing (or offerlisting) to then start a conversation with the seller of the listing
        Listing baseOrder =  em.find(Listing.class, listingId);
        if ( baseOrder == null || conversation == null) return null;
        conversation.setBaseOrder(baseOrder);
        return updateToDB(conversation);

    }

    /**
     * Adds a text message to a conversation; first the message is prepared before being added
     * to the conversation object. The participants is supposed to be notified by this function
     * @param sender a User; the sender of the message
     * @param message the message text to be sent
     * @param conversationId the ID for the conversation to be targeted
     * @return
     */
    public boolean sendMessageToConversation(User sender, String message, Long conversationId) {
        Conversation conversation = findConversationById(conversationId);
        if (conversation == null || sender == null || message == null) return false;
        Message msg = new Message();
        msg.setSender(sender);
        msg.setContent(message);
        if(addMessage(msg,conversation)) {
            // OK: notify participants and return OK == true
            System.out.println("CONTROL-CHAT: " + " MSG ADDED OK");
            return true;
        } else {
            System.out.println("CONTROL-CHAT: " + " MSG ADDED FAILURE");
            // FAIL: send ERROR to client and return fail
            return false;
        }

        // Find conversation
        // Create message object with sender
        // Add message to conversation, return true if OK

        // Notify other participants than sender
        // TODO: Use push to send notification to participants(future issue) - requires platform dependant compoent
    }

    public boolean sendMessageToListing(User sender, String message, Long listingId ) {
        Listing listing = ls.findListingById(listingId);
        // Find listing, return false if not exsiswt
        // Create a new conversation, (OR find the exsisting conversation ?)  and add it to the listing
        // Add the message to the conversation (use sendMessageToConversation above)
        // Write update to DB for the listing, and possibly conversation (how often shall we save / every message ?)

        return true;
    }

    //////// JPA SERVICE FUNCTIONS ////////

    /**
     *  Adds a message to the conversation, and saves the two to the persistence layer
     * @param message the Message to save
     * @param conversation the Conversation to add the message to
     * @return returns the saved Message if success, otherwise OK
     */
    boolean addMessage(Message message, Conversation conversation) {
        // Find the conversation by id
        // If found, add a message to the List inside the conversation
        Conversation foundConversation = getFromDB(conversation.getId());
        if (foundConversation == null || message == null) return false;
        List<Message> msgList = getMsgs(foundConversation);
        msgList.add(message);
        try {
            System.out.println("TRY PERSIST");
            em.persist(message);
            foundConversation = updateToDB(foundConversation);
            if(foundConversation == null) {
                return false; // FAILED
            } else {
                return true; // SUCCESS
            }
        } catch (PersistenceException pe) {
           return false;
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
            return em.merge(input);
        } catch (PersistenceException pe) {
            return null;
        }
    }

    private Conversation getFromDB(long input) {
        try {
           return em.find(Conversation.class, input);
        } catch (PersistenceException pe) {
            return null;
        }
    }


    private List<Message> getMsgs(Conversation conversation) {
        Conversation result = em.find(Conversation.class, conversation);
        if (result == null) return new ArrayList<>();
        List<Message> messageList = result.getMessages();
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }


}
