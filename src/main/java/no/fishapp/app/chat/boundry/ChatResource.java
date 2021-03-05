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

            List<ConversationDTO> conversationDTOS= currentUser.getUserConversations().stream()
                    .map(ConversationDTO::buildFromConversation)
                    .collect(Collectors.toList());
            response = Response.ok(conversationDTOS).build();
        } else {
            response = Response.serverError().build();
        }
        return response;
    }


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
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
            response = Response.notModified().build();
        }
        return response;
    }

    @POST
    @Path("{id}/send")
    @Valid
    @Consumes(MediaType.APPLICATION_JSON)
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
                if (!newMessageBody.getMessageText().isBlank() && !newMessageBody.getMessageText().isEmpty()) {
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

    @GET
    @Valid
    @Path("{id}/latest")
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

    @GET
    @Valid
    @Path("{id}/range")
    public Response getMessageRange(
            @NotNull @PathParam("id") Long conversationId,
            @NotNull @QueryParam("from") Long fromId,
            @NotNull @QueryParam("offset") Long offset
    ) {
        Response     response;
        Conversation conversation = chatService.getConversation(conversationId);

        if (conversation.isUserInConversation(userService.getLoggedInUser())) {
            System.out.println("adasd");
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
