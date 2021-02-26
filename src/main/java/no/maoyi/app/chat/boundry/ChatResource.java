package no.***REMOVED***.app.chat.boundry;

import no.***REMOVED***.app.auth.entity.Group;
import no.***REMOVED***.app.chat.control.ChatService;
import no.***REMOVED***.app.chat.entity.Conversation;
import no.***REMOVED***.app.chat.entity.ConversationDTO;
import no.***REMOVED***.app.chat.entity.Message;
import no.***REMOVED***.app.chat.entity.MessageDTO;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
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
            response = Response.ok(currentUser.getUserConversations()).build();
        } else {
            response = Response.serverError().build();
        }
        return response;
    }


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("start")
    @Valid
    public Response startConversationRequest(
            @NotNull @HeaderParam("listing") long listingId
    ) {
        // TODO: chek if user has a conv already on this comodity
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
    @Path("send")
    @Valid
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessageRequest(
            @NotNull @HeaderParam("conversation") long conversationId,
            @NotNull @HeaderParam("body") String messageBody
    ) {
        Response     response     = Response.serverError().build();
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            Conversation result = chatService.sendMessage(messageBody, conversation);
            if (result != null) {
                response = Response.ok(new ConversationDTO(result)).build();
            }
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    @GET
    @Valid
    @Path("messages/get")
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

    /// Får ikkje denne til å fungere, den viser alle meldingar, og tar ikkje hensyn til range
    /// Men den oppenfor (messages/get) fungerer med range.

    @GET
    @Valid
    @Path("messages/range")
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
            //FIXME: Returnerer samme resultat med messages (ufiltrert)
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }
}
