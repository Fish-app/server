package no.maoyi.app.chat.control;

import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.ConversationDTO;
import no.maoyi.app.chat.entity.Message;
import no.maoyi.app.listing.control.ListingService;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ChatService {

    @PersistenceContext
    EntityManager em;

    @Inject
    ListingService ls;

    /**
     *  needs to be fixed
     *  1. message list return (with filters)
     *  2. when a buyer creates a conversation looking on a sellers offerlisting,
     *     we need to add the seller to that conversation
     *
     *  future: handle pictures somehow
     *  future: handle push notfications outside of app
     */


    /**
     * Starts a new conversation, and saves it to the DB
     * @return the conversation
     */
    public Conversation newConversation() {
        Conversation input = new Conversation();
        try {
            em.persist(input);
            em.flush();
            return em.find(Conversation.class, input.getId());
        } catch (PersistenceException pe) {
            return null;
        }
    }


    /**
     *  Adds a conversation to a listing, useful for when creating a order as a buyer. We can then find the seller
     *  as the creator from the order listing.
     * @param conversation conversation object to modify
     * @param listingId id of listing to be added to the conversation object
     * @return the modified conversation object
     */
    public Conversation addConversationToListing(Conversation conversation, long listingId) {
        // Find a listing (or offerlisting) to then start a conversation with the seller of the listing
        Listing baseOrder =  em.find(Listing.class, listingId);
        if ( baseOrder == null || conversation == null) return null;
        try {
            conversation.setBaseOrder(baseOrder);
            return em.merge(conversation);
        } catch (PersistenceException pe) {
            return null;
        }

    }


    /**
     * Adds a text message to a conversation; first the message is prepared before being added
     * to the conversation object. Sender is added to conversation if they have not participated before.
     *
     * @param sender a User; the sender of the message
     * @param message the message text to be sent
     * @param conversationId the ID for the conversation to be targeted
     * @return the DTO to be used in the REST response
     */
    public ConversationDTO sendMessageToConversation(User sender, String message, Long conversationId) {
        Conversation input = em.find(Conversation.class, conversationId);
        if (input == null || sender == null || message == null) return null;
        Message msg = new Message();
        msg.setSender(sender);
        msg.setContent(message);
        // Add the message to the DB and add the user to the conversation list
        if(addMessage(msg,conversationId) && addUserToConversation(sender, conversationId)) {
            // OK: notify participants and return OK == true
            System.out.println("CONTROL-CHAT: " + " MSG ADDED OK");

            // Get a updated conversation object to return
            Conversation output = em.find(Conversation.class, conversationId);
            // TODO: Use push to send notification to participants(future issue)
            List<User> usersToNotify = output.getParticipants();
            //NotfifyParticipants(usersToNotify);
            // Notify other participants than sender (use user_has_conversations-sender to send push)

            return new ConversationDTO(output);
        } else {
            System.out.println("CONTROL-CHAT: " + " MSG ADDED FAILURE");
            // FAIL: send ERROR to client and return fail
            return null;
        }
    }


    // FIXME REMOVE?: I think this is not needed, as users know the conversation ID and uses that one instead
    public boolean sendMessageToListing(User sender, String message, Long listingId ) {
        Listing listing = ls.findListingById(listingId);
        // Find listing, return false if not exsiswt
        // Create a new conversation, (OR find the exsisting conversation ?)  and add it to the listing
        // Add the message to the conversation (use sendMessageToConversation above)
        // Write update to DB for the listing, and possibly conversation (how often shall we save / every message ?)

        return true;
    }

    // FIXME:
    private Message getLastMessageInConversation(Conversation conversation, User user) {
        List<Message> msgList = getMessagesInRangeInConversation(conversation, user, 0L, 0L);
        // Filter message list to only contain last message,
        // FIXME: possible check if named queries or more effective method can be used
        return null;
    }


    // FIXME:
    private List<Message> getMessagesInRangeInConversation(
            Conversation conversation,
            User principal,
            Long rangeStart,
            Long rangeEnd
    ) {
        if (isUserInConversation(conversation, principal)) {
            ConversationDTO dtoHolder = new ConversationDTO(); // initialiaze later, just before sending response.
            //TODO: Find out how to make a interval list in a effective way
            //TODO: CONTINUE HERE !!!!
            if (rangeEnd == null && rangeStart == null) {
                // generate list of mesgs in range interval

            } else {
                if (rangeEnd != null && rangeStart == null) {
                    // gen list of msgs from start to range end
                } else {
                    // gen list of msgs from end to range start
                }
            }

        } else {
            return null;
        }
        // Check that user is already participant (security),
        // If true, try to find conversation, start build message list

        // If both range param is null, return all messages in conversation
        // If range end is null, return messages from range start to latest mesg
        // If range start is null, return messages from start to range end.
        return null;
    }


    /**
     * Helper function to examine if user already is a participant for the
     * conversation. Used for security reasons
     * @param conversation conversation object to examine
     * @param userToCheck the user to check is a participant
     * @return true if the user is a participant of the conversation, otherwise false
     */
    private boolean isUserInConversation(Conversation conversation, User userToCheck) {
        em.refresh(conversation);
        em.refresh(userToCheck);
        List<User> exsistingParticipants = conversation.getParticipants();
        if (exsistingParticipants != null) {
            for (User userInConversation : exsistingParticipants
                 ) {
                if (userInConversation.getId() == userToCheck.getId()) {
                    return true; // STOP LOOP WHEN WE FOUND USER
                }
            }
        }
        return false;
    }

    private boolean addUserToConversation(User user, Long conversationId) {
        // Find conversation by id
        // Add user to the conversation if not already added
        Conversation conversation = em.find(Conversation.class, conversationId);
        if(user!= null && conversation != null) {
            if(isUserInConversation(conversation, user)) {
                System.out.println("CONTROL-CHAT: User is member of conversation");
                return true; // nothing needs to be done, user already in conversation
            } else {
                try {
                    // User ownes M-N join; therefore conversation is added to user
                    // and user is persisted to update SQL table state
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

    /**
     *  Adds a message to the conversation, and saves the two to the persistence layer
     * @param message the Message to save
     * @param conversationId the Conversation to add the message to
     * @return returns the saved Message if success, otherwise OK
     */
    private boolean addMessage(Message message, Long conversationId) {
        // Find the conversation by id
        // If found, add a message to the List inside the conversation
        Conversation foundConversation = em.find(Conversation.class, conversationId);
        if (foundConversation == null || message == null) return false;
        List<Message> msgList = foundConversation.getMessages();
        msgList.add(message);
        try {
            em.persist(message);
            em.flush();
            Conversation persistResult = em.merge(foundConversation);
            // SUCCESS
            return persistResult != null; // TRUE -> OK if result was not null
        } catch (PersistenceException pe) {
           return false;
        }
    }
}
