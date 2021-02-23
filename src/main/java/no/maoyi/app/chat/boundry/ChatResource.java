package no.maoyi.app.chat.boundry;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.chat.control.ChatService;
import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.ConversationDTO;
import no.maoyi.app.chat.entity.MessageDTO;
import no.maoyi.app.user.control.UserService;
import no.maoyi.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
@Path("chat")
public class ChatResource {

    @Inject
    ChatService service;

    @Inject
    UserService userService;


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("startconversation")
    public Response startConversationRequest(
            @FormParam("listing") Long listingId
    ) {
        Response response = Response.serverError().build();
        Conversation conversation = null;
        conversation = service.newConversation();
        if (conversation != null) {
            if (listingId != null) conversation = service.addConversationToListing(conversation, listingId);
            if (conversation != null) {
                response = Response.ok(conversation).build();
            }
        }
        return response;
    }

    /**
     * Send a message to a conversation by ID or a conversation associated with a listing ID
     *
     * @param conversationId the conversation to send the message to
     * @param messageBody    currently message text
     * @return 200 OK if successful, with the conversation DTO. App can use math message count
     * to find out how many messages it needs to ask for in @GET getmessages()
     */
    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Path("sendmessage")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessageRequest(
            @FormParam("conversation") Long conversationId,
            @FormParam("listing") Long listingId,
            @FormParam("body") String messageBody
            // TODO: Change body to a more fitting type
    ) {
        // 500 will be called on failure (Pajara or Jakarta will return 500 on exception)
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        User senderUser = userService.getLoggedInUser();

        // return 401 if not logged in
        if (senderUser == null) response = Response.status(Response.Status.UNAUTHORIZED).build();

        if ((conversationId != null)) {
            // sender knows conversation id, and wants to send message directly
            System.out.println("RESOURCE-CHAT: msg -> conversation");
            ConversationDTO result = service.sendMessageToConversation(senderUser, messageBody, conversationId);
            if (result != null) {
                System.out.println("RESOURCE-CHAT: 200 OK");
                response = Response.ok(result).build();
            } else {
                response = Response.notModified().build();
            }
        } else {
            // bad request (either both or none are specified)
            return response;
        }
        return response;
    }


    /**
     * Returns a list of the messages the client asked for
     *
     * @param conversationId the id of the conversation to find messages in
     * @param rangeEnd       .
     * @param rangeStart     .
     * @return a conversation DTO holding list of messages, or a plain JSON list of List(Message)  ?
     * probably better with a plain list, as the conversation DTO was returned before
     */
    @GET
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    @Path("getmessages")
    public Response getFilteredMessagesInConversation(
            @QueryParam("conversation") Long conversationId,
            @QueryParam("to") Long rangeEnd,
            @QueryParam("from") Long rangeStart
    ) {
        User receiver = userService.getLoggedInUser();
        Response response = Response.ok().build();

        // 401 if not logged in
        if (receiver == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        // 1. check if receiver is member of conversation
        // 2. check if range parameters is specified, and filter the message list
        //    otherwise return all messages in conversation
        // 3. Return the list of messages in the response

        // e.g.
        //List<MessageDTO> msgList = service.getMessagesInRange(receiver, conversationId, rangeStart, rangeEnd);
        //response = Response.ok(msgList).build();

        return response;
    }
}
