package no.fishapp.chat.control;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.chat.client.StoreClient;
import no.fishapp.chat.model.Conversation;
import no.fishapp.chat.model.ConversationDTO;
import no.fishapp.chat.model.Message;
import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.util.restClient.exceptionHandlers.RestClientHttpException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Control-part of the Chat-microservice
 * Manages sending and receiving {@link Message}s and {@link Conversation}s,
 * and storage towards the database.
 * Uses {@link EntityManager} for communicating with the database.
 * Uses {@link StoreClient} to communicate with the Store-microservice
 */
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


    private static final String ERR_MSG_JWT_TOKEN = "Error reading JWT token";
    private static final String DO_USER_HAVE_CONV = "select count (rt) from Rating rt where  rt.issuerId = :isu_id and  rt.userRatedId = :rtd_id and rt.ratedTransactions.id = :t_id";
    private static final String GET_USER_CONVS = "select cv from Conversation cv where  cv.conversationStarterUserId = :uid OR cv.listingCreatorUserId = :uid";
    private static final String GET_CONV = "select cv from Conversation cv where cv.listingId = :lid AND cv.conversationStarterUserId = :uid";

    /**
     * Retrieves a list of the conversations belonging to a user of the specified ID.
     * @param id of the user
     * @return A list holding the conversations. If none was found, we return NULL
     */
    public List<Conversation> getUserConversations(long id) {
        var query = entityManager.createQuery(GET_USER_CONVS, Conversation.class);
        query.setParameter("uid", id);

        try {
            return query.getResultList();
        } catch (NoResultException ignore) {
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a specific conversation between a user and a listing.
     * @param uid The user involved with the conversation
     * @param listingId - The listing the {@link Conversation }is related to
     * @return
     */
    private Optional<Conversation> getUserListingConversation(long uid, long listingId) {
        TypedQuery<Conversation> query = entityManager.createQuery(GET_CONV, Conversation.class);

        query.setParameter("uid", uid);
        query.setParameter("lid", listingId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ignore) {
            return Optional.empty();
        }
    }


    /**
     * Build a list of all the current {@link Conversation }the {@link User} is associated with.
     * Used in the app to display a list of conversations with previews of last sent message.
     * @param includeLastMsg true/false to enable/disable the inclusion of the last message
     * @return The list of all current {@link Conversation}.
     */
    public List<ConversationDTO> getCurrentUserConversations(boolean includeLastMsg) {
        long               currentUserId;

        /**
         *  Verifiy that a token exsists to find the user ID.
         *  If the token is missing we cancel the operation and return an empty list.
         */
        if (jwtSubject.get().isPresent()) {
            currentUserId = Long.parseLong(jwtSubject.get().get());
        } else {
            log.log(Level.SEVERE,ERR_MSG_JWT_TOKEN);
            return new ArrayList<>();
        }
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
     * Create a new {@link Conversation} or finds the exsiting {@link Conversation}
     * assocatied with an {@link no.fishapp.store.model.listing.Listing}. The function
     * is executed when a user requests to start a Chat. The ID of the listing is known, as
     * a Chat is always started from a context where the listing is known.
     *
     * @param listingId The identifier that selects the {@link no.fishapp.store.model.listing.Listing}
     * @return The already existing or newly created {@link Conversation}.
     */
    public Optional<Conversation> newListingConversation(long listingId) {
        long userId;

        if(jwtSubject.get().isPresent()) {
            userId = Long.parseLong(jwtSubject.get().get());
        } else {
            log.log(Level.SEVERE,ERR_MSG_JWT_TOKEN);
            return Optional.empty();
        }
        Optional<Conversation> userConv = this.getUserListingConversation(userId, listingId);

        if (userConv.isPresent()) {
            return userConv;
        } else {
            try {
                CompletableFuture<ChatListingInfo> isValidFuture = storeClient.getListing(listingId).toCompletableFuture();
                Conversation conversation  = new Conversation();

                conversation.setListingId(listingId);
                conversation.setConversationStarterUserId(userId);

                ChatListingInfo listing = isValidFuture.join();
                // Check if the listing is enabled on the market (is open for sale)
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
        Conversation conv = entityManager.find(Conversation.class, convId);
        if (conv == null) {
            return Optional.empty();
        } else {
            return Optional.of(conv);
        }
    }

    public Optional<Conversation> sendMessage(String messageBody, long conversationId) {
        long userId;
        if (jwtSubject.get().isPresent()) {
            userId = Long.parseLong(jwtSubject.get().get());
        } else {
            log.log(Level.SEVERE,ERR_MSG_JWT_TOKEN);
            return Optional.empty();
        }

        Optional<Conversation> conversation = this.getConversation(conversationId);

        if (conversation.map(cnv -> cnv.isUserInConv(userId)).orElse(false) && !messageBody.isEmpty()) {

            Message message = new Message();
            message.setContent(messageBody);
            message.setSenderId(userId);
            entityManager.persist(message);

            Conversation updatedConv = conversation.get();
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
            log.log(Level.SEVERE,ERR_MSG_JWT_TOKEN);
            return Optional.empty();
        }

       long userId = Long.parseLong(jwtSubject.get().get());
        Optional<Conversation> conversation = this.getConversation(convId);

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
