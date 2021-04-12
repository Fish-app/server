package no.fishapp.chat.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.chat.client.StoreClient;
import no.fishapp.chat.model.Conversation;
import no.fishapp.chat.model.ConversationDTO;
import no.fishapp.chat.model.Message;
import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.util.restClient.exceptionHandlers.RestClientHttpException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
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

@Log
@Transactional
@RequestScoped
public class ChatService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    @RestClient
    StoreClient storeClient;


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

    private Optional<Conversation> getUserListingConversation(long uid, long listingId) {
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

        return userConvs.stream().map(conversation -> (includeLastMsg) ?
                ConversationDTO.buildFromConversation(conversation, conversation.getFirstMessage().orElse(null)) :
                ConversationDTO.buildFromConversation(conversation)).collect(Collectors.toList());
    }

    /**
     *  todo: handle pictures somehow
     *  todo: handle push notfications outside of app
     */

    /**
     * creates a new conversation between the current user and the owner of the listing with the provided id
     *
     * @param listingId the listing to hav conversation about
     */
    public Optional<Conversation> newListingConversation(long listingId) {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long userId   = Long.parseLong(jwtSubject.get().get());
        var  userConv = this.getUserListingConversation(userId, listingId);

        if (userConv.isPresent()) {
            return userConv;

        } else {
            try {
                var          isValidFuture = storeClient.getListing(listingId).toCompletableFuture();
                Conversation conversation  = new Conversation();

                conversation.setListingId(listingId);
                conversation.setConversationStarterUserId(userId);

                ChatListingInfo listing = isValidFuture.join();
                if (!listing.getIsOpen()) {
                    return Optional.empty();
                }

                conversation.setListingId(listingId);
                conversation.setListingCreatorUserId(listing.getCreatorId());

                entityManager.persist(conversation);

                return Optional.of(conversation);

            } catch (RestClientHttpException e) {
                return Optional.empty();
            }
        }
    }


    public Optional<Conversation> getConversation(long convId) {
        var conv = entityManager.find(Conversation.class, convId);
        if (conv == null) {
            return Optional.empty();
        } else {
            return Optional.of(conv);
        }
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


    public Optional<Conversation> sendMessage(String messageBody, long conversationId) {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long userId       = Long.parseLong(jwtSubject.get().get());
        var  conversation = this.getConversation(conversationId);

        if (conversation.map(cnv -> cnv.isUserInConv(userId)).orElse(false) && !messageBody.isEmpty()) {

            Message message = new Message();
            message.setContent(messageBody);
            message.setSenderId(userId);
            entityManager.persist(message);

            var updatedConv = conversation.get();
            updatedConv.addMessage(message);

            entityManager.persist(conversation.get());

            return Optional.of(updatedConv);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns all messages newer than the message with the provided id.
     *
     * @param convId        the id of the concversation wo chek
     * @param fromMessageId (exclusive) return messages newer than the message with this id
     * @return a list with the messages
     */


    public Optional<List<Message>> getMessagesTo(long convId, long fromMessageId) {
        if (jwtSubject.get().isEmpty()) {
            log.log(Level.SEVERE, "Error reading jwt token");
            return Optional.empty();
        }

        long userId       = Long.parseLong(jwtSubject.get().get());
        var  conversation = this.getConversation(convId);

        if (conversation.map(cnv -> cnv.isUserInConv(userId)).orElse(false)) {
            return Optional.of(conversation.get().getMessages()
                                           .stream()
                                           .filter(message -> message.getId() > fromMessageId)
                                           .collect(
                                                   Collectors.toCollection(ArrayList::new)));

        } else {
            return Optional.empty();
        }

    }


}
