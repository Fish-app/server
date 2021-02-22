package no.***REMOVED***.app.chat.control;

import no.***REMOVED***.app.chat.entity.Conversation;
import no.***REMOVED***.app.chat.entity.ConversationDTO;
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
    //TODO: Cleanup and push warnings

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
    public ConversationDTO sendMessageToConversation(User sender, String message, Long conversationId) {
        System.out.println("SEND MESGCLLAED");
        Conversation conversation = findConversationById(conversationId);
        System.out.println("CONVERSATATION OKAY");
        if (conversation == null || sender == null || message == null) return null;
        System.out.println("nothing wass null");
        Message msg = new Message();
        msg.setSender(sender);
        msg.setContent(message);
        msg.setConversation(conversation);
        // Add the message to the DB and add the user to the conversation list
        if(addMessage(msg,conversationId) && addUser(sender, conversationId)) {
            // OK: notify participants and return OK == true
            System.out.println("CONTROL-CHAT: " + " MSG ADDED OK");
            return new ConversationDTO(findConversationById(conversationId));
        } else {
            System.out.println("CONTROL-CHAT: " + " MSG ADDED FAILURE");
            // FAIL: send ERROR to client and return fail
            return null;
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

    boolean addUser(User user, Long conversationId) {
        // Find conversation by id
        // Add user to the conversation if not already added
        Conversation conversation = findConversationById(conversationId);
        if(user!= null && conversation != null) {
            if(isUserInConversation(conversation, user)) {
                System.out.println("CONTROL-CHAT: User is member of conversation");
                return true; // all good, user already in conversation
            } else {
                try {
                    // need to add user
                    List<Conversation> userConversations = user.getUserConversations();
                    userConversations.add(conversation);
                    em.merge(user);
                    em.flush();
                    System.out.println("CONTROL-CHAT: User was added to conversation");
                    return true;

                } catch (PersistenceException pe) {
                    return false;
                }
            }
        }
        return false;
    }

    boolean isUserInConversation(Conversation conversation, User userToCheck) {
        em.refresh(conversation);
        em.refresh(userToCheck);
        List<User> exsistingParticipants = conversation.getParticipants();
        if (exsistingParticipants != null) {
            for (User userInConversation : exsistingParticipants
                 ) {
                if (userInConversation.getId() == userToCheck.getId()) {
                    System.out.println(userInConversation.getId());
                    System.out.println("==");
                    System.out.println(userToCheck.getId());

                    return true; // STOP LOOP WHEN WE FOUND USER
                }
            }
        }
        return false;
    }

    /**
     *  Adds a message to the conversation, and saves the two to the persistence layer
     * @param message the Message to save
     * @param conversation the Conversation to add the message to
     * @return returns the saved Message if success, otherwise OK
     */
    boolean addMessage(Message message, Long conversation) {
        // Find the conversation by id
        // If found, add a message to the List inside the conversation
        Conversation foundConversation = getFromDB(conversation);
        if (foundConversation == null || message == null) return false;
        List<Message> msgList = getMsgs(foundConversation.getId());
        msgList.add(message);
        //TODO: This function may be removed for line: 83
        //foundConversation.setMessages(msgList);
        try {
            em.persist(message);
            foundConversation = updateToDB(foundConversation);
            if(foundConversation == null) {
                return false; // FAILED
            } else {
                return true; // SUCCESS
            }
        } catch (PersistenceException pe) {
            System.out.println("CONTROL-CHAT: Persistence failure");
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

    private Conversation getFromDB(long conversationId) {
        try {
           return em.find(Conversation.class, conversationId);
        } catch (PersistenceException pe) {
            return null;
        }
    }


    private List<Message> getMsgs(long conversationId) {
        Conversation result = em.find(Conversation.class, conversationId);
        if (result == null) return new ArrayList<>();
        List<Message> messageList = result.getMessages();
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }


}
