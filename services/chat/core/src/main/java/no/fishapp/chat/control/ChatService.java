package no.fishapp.chat.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.chat.model.Conversation;
import no.fishapp.chat.model.ConversationDTO;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Transactional
@Log
public class ChatService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;


    @Inject
    SecurityContext securityContext;

    private static final String DO_USER_HAVE_CONV = "select count (rt) from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";

    private static final String GET_USER_CONVS = "select cv from Conversation cv where  cv.conversationStarterUserId = :uid OR cv.listingCreatorUserId = :uid";

    private static final String GET_CONV = "select cv from Conversation cv where cv.listingId = :lid AND cv.conversationStarterUserId = :uid";

    public List<Conversation> getUserConversations(long id) {
        var query = entityManager.createQuery(GET_USER_CONVS, Conversation.class);
        query.setParameter("uid", id);

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
        }
        return null;
    }

    public Optional<Conversation> getUserConversation(long uid, long listingId) {
        var query = entityManager.createQuery(GET_CONV, Conversation.class);

        query.setParameter("uid", uid);
        query.setParameter("lid", listingId);
        try {
            return Optional.of(query.getSingleResult());

        } catch (NoResultException ignore) {
        }
        return Optional.empty();
    }


    public List<ConversationDTO> getCurrentUserConversations(boolean includeLastMsg) {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return null;
        }

        long               currentUserId = Long.parseLong(jwtSubject.get().get());
        List<Conversation> userConvs     = this.getUserConversations(currentUserId);

        return userConvs.stream().map(conversation -> {
            return (includeLastMsg) ?
                   ConversationDTO.buildFromConversation(conversation, conversation.getFirstMessage().orElse(null)) :
                   ConversationDTO.buildFromConversation(conversation);

        }).collect(Collectors.toList());
    }

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
            if (listing == null) {
                return null; // Avoid nullptr below
            }
            User         listingOwner        = listing.getCreator();
            User         conversationStarter = userService.getLoggedInUser();
            Conversation conversation        = new Conversation(listing, conversationStarter);
            entityManager.persist(conversation);

            userService.addConversationToUser(conversation, listingOwner);
            userService.addConversationToUser(conversation, conversationStarter);
            return conversation;
        } catch (PersistenceException pe) {
            return null;
        }
    }

    /**
     * Returns the conversation with the provided id
     *
     * @param convId the conversations id
     * @return the conversation, null if not found
     */
    public Conversation getConversation(long convId) {
        return entityManager.find(Conversation.class, convId);
    }

    /**
     * Return the message with the provided ID. Used in app to get last message preview
     *
     * @param messageId the message id
     * @return the conversation, null if not found
     */
    public Message getMessage(long messageId) {
        return entityManager.find(Message.class, messageId);
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
        System.out.println("MESSAGEZS IS NULL");
        if (messages == null) {
            return new ArrayList<>();
        }
        Message anchor = entityManager.find(Message.class, fromId);
        //Message anchor = messages.stream().filter(message -> message.getId() == fromId).findFirst().get();
        int elementIndex = messages.indexOf(anchor);

        if (offset == null) {
            offset = 0L; // default value if null
        }

        System.out.println("OFFSET " + offset.toString());
        System.out.println("FROMID " + fromId.toString());
        System.out.println("CONVID " + convId.toString());
        System.out.println("ELEMENTINDEKS " + elementIndex);

        if (offset >= 0L) {
            return messages.subList(elementIndex, (int) Math.min(messages.size() - 1, elementIndex + offset));
        } else {
            return messages.subList((int) Math.max(0, elementIndex - offset), elementIndex);
        }
    }
}
