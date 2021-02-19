package no.maoyi.app.chat.boundry;

import no.maoyi.app.auth.entity.Group;
import no.maoyi.app.chat.control.ChatService;
import no.maoyi.app.chat.entity.Conversation;

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
            if(listingId != null) conversation = service.attachConversation(conversation,listingId);
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
            @FormParam("listing") Long listingId
    ) {
        Response response = Response.serverError().build();

        if ((conversationId == null) ^ (listingId == null)) {
            // do request
            // if conversationid is null, listing must not be null and vice versa
            // if null, send the message to
            (conversationId != null) ? sendMessageToConversation() : sendMessageToListing();
       } else {
            // bad request (either both or none are specified)
            return response;
       }
    }

}
