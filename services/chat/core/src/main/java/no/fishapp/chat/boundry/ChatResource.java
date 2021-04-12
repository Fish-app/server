package no.fishapp.chat.boundry;


import no.fishapp.auth.model.Group;
import no.fishapp.chat.control.ChatService;
import no.fishapp.chat.model.MessageBody;
import no.fishapp.chat.model.MessageDTO;

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
import java.util.stream.Collectors;

@Path("/")
@RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
public class ChatResource {

    public static final String UTF8_CHARSET = "; charset=utf-8";

    @Inject
    ChatService chatService;

    /**
     * Returns a list of elements with Conversation to the current
     * authenticated user. Used in app to display the current users conversation
     *
     * @param includeLastMsg if true, attach the last message
     * @return a json encoded list of ConversationDTOS
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

        return Response.ok(chatService.getCurrentUserConversations(includeLastMsg)).build();
    }


    /**
     * Used to start a new conversation and associate it with a listing
     *
     * @param listingId The listing ID associated with the conversation
     * @return A ConversationDTO holding metadata about the new conversation,
     * If conversation already exsist, we return the existing conversation.
     */

    // TODO: change name/path? mere korrekt og kall den for get conversation siden det e hva som skjer 90% av tia
    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Produces(MediaType.APPLICATION_JSON + UTF8_CHARSET)
    @Path("new/{id}")
    public Response startConversationRequest(
            @NotNull @PathParam("id") long listingId
    ) {

        var userConv = chatService.newListingConversation(listingId);

        return userConv.map(Response::ok)
                       .orElse(Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR)).build();

    }

    /**
     * Used to send a new message to a Conversation
     *
     * @param conversationId the conversation to send the message to
     * @param newMessageBody a JSON encoded MessageBody
     * @return the updated ConversationDTO in JSON with new metadata, such as new latest message Id.
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
        var userConv = chatService.sendMessage(newMessageBody.getMessageText(), conversationId);

        return userConv.map(Response::ok)
                       .orElse(Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR)).build();
    }


    /**
     * Returns the list of messages in a conversation. The starting point of the list can be
     * specified with a message id. For example in a conversation with 5 messages [1,2,3,4,5],
     * setting lastId to 3 will return a list containing message [4,5]
     *
     * @param conversationId id of the conversation
     * @param lastId         message Id if the last message to start the list from
     * @return a json encoded list of MessageDTO elements
     */
    @GET
    @Valid
    @Path("{id}/updates")
    @Produces({MediaType.APPLICATION_JSON + UTF8_CHARSET})
    public Response updatesQuery(
            @NotNull @PathParam("id") long conversationId,
            @NotNull @QueryParam("last-id") long lastId
    ) {
        var messages = chatService.getMessagesTo(conversationId, lastId);


        if (messages.isPresent()) {
            List<MessageDTO> messageDTOS = messages.get().stream()
                                                   .map(MessageDTO::buildFromMessage)
                                                   .collect(Collectors.toList());

            return Response.ok(messageDTOS).build();
        } else {
            // todo: maby just propegate the 404 if wrong id is given, but if that happens somone is probably not beeing nice
            return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
