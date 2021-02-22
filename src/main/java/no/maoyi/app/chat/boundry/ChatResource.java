package no.maoyi.app.chat.boundry;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.chat.control.ChatService;
import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.ConversationDTO;
import no.maoyi.app.user.control.UserService;
import no.maoyi.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("chat")
public class ChatResource {

    @Inject
    ChatService service;

    @Inject
    UserService userService;


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME,Group.ADMIN_GROUP_NAME})
    @Path("start")
    public Response startConversationRequest(
            @FormParam("listing") Long listingId
    ) {
        Response response = Response.serverError().build();
        Conversation conversation = null;
        conversation = service.newConversation();
        if (conversation != null) {
            if(listingId != null) conversation = service.addConversationToListing(conversation,listingId);
            if (conversation != null) {
                response = Response.ok(conversation).build();
            }
        }
        return response;
    }

    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME,Group.ADMIN_GROUP_NAME})
    @Path("message")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessageRequest(
            @FormParam("conversation")  Long conversationId,
            @FormParam("listing") Long listingId,
            @FormParam("body") String messageBody
            // TODO: Change body to a more fitting type
    ) {
        // 500 will be called on failure (Pajara or Jakarta will return 500 on exception)
        Response response = Response.status(Response.Status.BAD_REQUEST).build();

        if ((conversationId == null) ^ (listingId == null)) {
            // do request
            // if conversationid is null, listing must not be null and vice versa
            User senderUser = userService.getLoggedInUser();
            if ((conversationId != null)) {
                // sender knows conversation id, and wants to send message directly
                System.out.println("RESOURCE-CHAT: msg -> conversation");
                ConversationDTO result = service.sendMessageToConversation(senderUser, messageBody, conversationId);
                if(result != null) {
                    System.out.println("RESOURCE-CHAT: 200 OK");
                    response = Response.ok(result).build();
                } else {
                    response = Response.notModified().build();
                }
            } else {
                // TODO: To be implemented if deemed nessecary
                // sender knows listing id, and wants to send message to conversation,
                // by being identified with userid and listing (1 user can start 1 conversation on 1 listing) (overkill?)
                System.out.println("RESOURCE-CHAT: msg -> listing");
                service.sendMessageToListing(senderUser, messageBody, listingId);
            }
        } else {
            // bad request (either both or none are specified)
            return response;
       }
        return  response;
    }

}
