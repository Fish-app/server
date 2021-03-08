package no.fishapp.app.chat.boundry;

import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.chat.entity.*;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.chat.control.ChatService;
import no.fishapp.app.user.entity.User;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Path("chat")
@RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})

public class ChatResource {

    public static final String UTF8_CHARSET= "; charset=utf-8";

    @Inject
    ChatService chatService;

    @Inject
    UserService userService;

    /**
     *  Returns a list of elements with Conversation to the current
     *  authenticated user. Used in app to display the current users conversation
     * @return a json encoded list of ConversationDTOS
     */
    @GET
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("myconversations")
    @Valid
    public Response getCurrentUserConversationsRequest(
    ) {
        Response response;
        User     currentUser = userService.getLoggedInUser();
        if (currentUser != null) {

            List<ConversationDTO> conversationDTOS= currentUser.getUserConversations().stream()
                    .map(ConversationDTO::buildFromConversation)
                    .collect(Collectors.toList());
            response = Response.ok(conversationDTOS).build();
        } else {
            response = Response.serverError().build();
        }
        return response;
    }


    /**
     *  Used to start a new conversation and associate it with a listing
     * @param listingId The listing ID associated with the conversation
     * @return A ConversationDTO holding metadata about the new conversation,
     * If conversation already exsist, we return the existing conversation.
     */
    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Produces(MediaType.APPLICATION_JSON+UTF8_CHARSET)
    @Path("new/{id}")
    public Response startConversationRequest(
           @NotNull @PathParam("id") long listingId
    ) {
        Response     response;
        Conversation conversation   = null;
        User         user           = userService.getLoggedInUser();
        boolean      hasListingConv = user.getUserConversations()
                                          .stream()
                                          .anyMatch(userConv -> userConv.getConversationListing().getId() == listingId);
        if (!hasListingConv) {
            conversation = chatService.newListingConversation(listingId);
            if (conversation != null) {
                response = Response.ok(ConversationDTO.buildFromConversation(conversation)).build();
            } else {
                response = Response.serverError().build();
            }
        } else {
            // Find the existing conversations and return the on with matching listing id.
            List<ConversationDTO> conversationDTOS = user.getUserConversations().stream()
                    .map(ConversationDTO::buildFromConversation)
                    .collect(Collectors.toList());
            for (ConversationDTO existingConversationDTO : conversationDTOS) {
                if(existingConversationDTO.getListing().getId() == listingId) {
                    Conversation reload = chatService.getConversation(existingConversationDTO.getId());
                    return Response.ok(ConversationDTO.buildFromConversation(reload)).build();
                }
            }
            response = Response.notModified().build();
        }
        return response;
    }

    /**
     * Used to send a new message to a Conversation
     * @param conversationId the conversation to send the message to
     * @param newMessageBody a JSON encoded MessageBody
     * @return the updated ConversationDTO in JSON with new metadata, such as new latest message Id.
     */
    @POST
    @Path("{id}/send")
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON+UTF8_CHARSET})
    public Response sendMessageRequest(
            @NotNull @PathParam("id") long conversationId,
            MessageBody newMessageBody
    ) {
        Response     response     = Response.serverError().build();
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            if(newMessageBody.getMessageText() == null) {
               response = Response.status(Response.Status.FORBIDDEN).build(); //invalid messsage body
            } else {
                if (!newMessageBody.getMessageText().isBlank()) {
                    Conversation result = chatService.sendMessage(newMessageBody.getMessageText(), conversation);
                    if (result != null) {
                        response = Response.ok(ConversationDTO.buildFromConversation(result)).build();
                    }
                }
            }
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    /**
     * Get the latest message of a conversation. Used in lists for previewing
     * the latest message,
     * @param conversationId the id of the conversation, user must already be a member here
     * @return the latest MessageDTO encoded in json if the conversation exist and user has access
     */
    @GET
    @Valid
    @Path("{id}/latest")
    @Produces({MediaType.APPLICATION_JSON+UTF8_CHARSET})
    public Response getLastMessage(
        @NotNull @PathParam("id") long conversationId
    ) {

        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
                List<Message> messages = chatService.getMessagesTo(conversationId, conversation.getLastMessageId() -1);
                List<MessageDTO> messageDTOS = messages.stream()
                        .map(MessageDTO::buildFromMessage)
                        .collect(Collectors.toList());
                MessageDTO lastMSG = messageDTOS.get(messageDTOS.size() - 1);
                if(lastMSG != null) {
                    response = Response.ok(lastMSG).build();
                } else {
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    /**
     * Returns the list of messages in a conversation. The starting point of the list can be
     * specified with a message id. For example in a conversation with 5 messages [1,2,3,4,5],
     * setting lastId to 3 will return a list containing message [4,5]
     * @param conversationId id of the conversation
     * @param lastId message Id if the last message to start the list from
     * @return a json encoded list of MessageDTO elements
     */
    @GET
    @Valid
    @Path("{id}/updates")
    @Produces({MediaType.APPLICATION_JSON+UTF8_CHARSET})
    public Response updatesQuery(
            @NotNull @PathParam("id") long conversationId,
            @NotNull @QueryParam("last-id") long lastId
    ) {
        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            if (conversation.getLastMessageId() == lastId) {
                response = Response.ok().build();
            } else {
                List<Message> messages = chatService.getMessagesTo(conversationId, lastId);
                List<MessageDTO> messageDTOS = messages.stream()
                                                       .map(MessageDTO::buildFromMessage)
                                                       .collect(Collectors.toList());
                response = Response.ok(messageDTOS).build();
            }
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    /**
     *  Returns an interval of messages in a JSON list
     * @param conversationId the conversation id to get messages for
     * @param fromId lower limit to start get messages from
     * @param offset the offset
     * @return a json list of message DTO elements
     */
    //FIXME: Not properly tested
    @GET
    @Valid
    @Path("{id}/range")
    @Produces(MediaType.APPLICATION_JSON+UTF8_CHARSET)
    public Response getMessageRange(
            @NotNull @PathParam("id") Long conversationId,
            @NotNull @QueryParam("from") Long fromId,
            @NotNull @QueryParam("offset") Long offset
    ) {
        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            List<Message> messages = chatService.getMessageRange(conversationId, fromId, offset);
            List<MessageDTO> messageDTOS = messages.stream()
                                                   .map(MessageDTO::buildFromMessage)
                                                   .collect(Collectors.toList());
            response = Response.ok(messageDTOS).build();
            //FIXME: Returnerer samme resultat med messages (ufiltrert)
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }
}
