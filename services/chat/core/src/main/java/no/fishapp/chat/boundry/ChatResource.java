package no.fishapp.chat.boundry;


import no.fishapp.auth.model.Group;
import no.fishapp.chat.control.ChatService;
import no.fishapp.chat.model.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * Boundary-part of the Chat-microservice
 * Implementes the REST HTTP API for the Chat-component of the Microservice
 *
 * Uses {@link ChatService} to process the requests.
 */
@Path("/")
@RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
public class ChatResource {

    // Used to declare UTF-8 in the header; used by clients when decoding data
    public static final String UTF8_CHARSET = "; charset=utf-8";

    @Inject
    ChatService chatService;

    /**
     * Returns a list of elements with Conversation to the current
     * authenticated {@link no.fishapp.user.model.user.User}.
     * Used in app to display the current users {@code Conversation}
     *
     * @param includeLastMsg if true, attach the last message
     * @return a json encoded list of {@link ConversationDTO}
     */
    @GET
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Produces(MediaType.APPLICATION_JSON + UTF8_CHARSET)
    @Path("conversations")
    @Valid
    public Response getCurrentUserConversationsRequest(
            @QueryParam("include-lastmessage") Boolean includeLastMsg
    ) {
        if (includeLastMsg == null) {
            includeLastMsg = false;
        }
        List<ConversationDTO> chatMsgs = chatService.getCurrentUserConversations(includeLastMsg);
        if (!chatMsgs.isEmpty()) {
            return Response.ok(chatMsgs).build();
        } else {
            return Response.noContent().build();
        }

    }
    /**
     * Used to start a new {@link Conversation} and associate it with a {@link no.fishapp.store.model.listing.Listing}
     * If {@code Conversation} already is present, we return the existing {@code Conversation}.
     *
     * @param listingId The {@code Listing}ID associated with the {@code Conversation}
     * @return A {@link ConversationDTO}holding metadata about the new {@code Conversation}.
     */
    // TODO: change name/path? mere korrekt og kall den for get conversation siden det e hva som skjer 90% av tia
    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Produces(MediaType.APPLICATION_JSON + UTF8_CHARSET)
    @Path("new/{id}")
    public Response startConversationRequest(
            @NotNull @PathParam("id") long listingId
    ) {
        Optional<Conversation> userConv = chatService.newListingConversation(listingId);
        return userConv.map(Response::ok)
                       .orElse(Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR)).build();

    }

    /**
     * Used to send a new {@link Message} to a {@link Conversation}
     * {@link ConversationDTO} is returned to ensure limited exposure of data,
     * such that not the entire {@code Conversation} is exposed.
     *
     * @param conversationId the {@link Conversation }to send the message to
     * @param newMessageBody a JSON encoded {@link MessageBody} holding the contents of a {@code Message}
     * @return the updated {@code ConversationDTO} in JSON with new metadata, such as new latest message Id.
     */
    @POST
    @Path("{id}/send")
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON + UTF8_CHARSET})
    public Response sendMessageRequest(
            @NotNull @PathParam("id") long conversationId,
            MessageBody newMessageBody
    ) {
        Optional<Conversation> userConv = chatService.sendMessage(newMessageBody.getMessageText(), conversationId);

        return userConv.map(Response::ok)
                       .orElse(Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR)).build();
    }

    /**
     * Returns the list of messages in a {@link no.fishapp.chat.model.Conversation}. The starting point of the list can be
     * specified with a {@link Message} id. For example in a conversation with 5 messages [1,2,3,4,5],
     * setting lastId to 3 will return a list containing message [4,5]
     *
     * @param conversationId id of the conversation
     * @param lastId         message Id if the last message to start the list from
     * @return a json encoded list of {@link MessageDTO} elements
     */
    @GET
    @Valid
    @Path("{id}/updates")
    @Produces({MediaType.APPLICATION_JSON + UTF8_CHARSET})
    public Response updatesQuery(
            @NotNull @PathParam("id") long conversationId,
            @NotNull @QueryParam("last-id") long lastId
    ) {
        Optional<List<Message>> messages = chatService.getMessagesTo(conversationId, lastId);

        if (messages.isPresent()) {
            List<MessageDTO> messageDTOS = messages.get().stream()
                                                   .map(MessageDTO::buildFromMessage)
                                                   .collect(Collectors.toList());
            return Response.ok(messageDTOS).build();
        } else {
            return Response.ok().status(Response.Status.NOT_FOUND).build();
        }
    }

}
