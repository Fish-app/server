package no.maoyi.app.chat.boundry;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.chat.control.ChatService;
import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.ConversationDTO;
import no.maoyi.app.chat.entity.Message;
import no.maoyi.app.chat.entity.MessageDTO;
import no.maoyi.app.user.control.UserService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Path("chat")
@RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})

public class ChatResource {

    @Inject
    ChatService chatService;

    @Inject
    UserService userService;


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("startconversation")
    public Response startConversationRequest(
            @NotNull @HeaderParam("listing") long listingId
    ) {
        Response     response;
        Conversation conversation = null;
        conversation = chatService.newListingConversation(listingId);
        if (conversation != null) {
            response = Response.ok(new ConversationDTO(conversation)).build();
        } else {
            response = Response.serverError().build();
        }
        return response;
    }

    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessageRequest(
            @NotNull @HeaderParam("conversation") long conversationId,
            @NotNull @HeaderParam("body") String messageBody
    ) {
        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            chatService.sendMessage(messageBody, conversation);
            response = Response.ok().build();
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    @GET
    @Path("mabyUpdates")
    public Response updatesQuery(
            @NotNull @HeaderParam("conversation") long conversationId,
            @NotNull @HeaderParam("last-id") long lastId
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


    public Response getMessageRange(
            @NotNull @HeaderParam("conversation") long conversationId,
            @NotNull @HeaderParam("from") long fromId,
            @NotNull @HeaderParam("offset") long offset
    ) {
        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            List<Message> messages = chatService.getMessageRange(conversationId, fromId, offset);
            List<MessageDTO> messageDTOS = messages.stream()
                                                   .map(MessageDTO::buildFromMessage)
                                                   .collect(Collectors.toList());
            response = Response.ok(messageDTOS).build();
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }
}
