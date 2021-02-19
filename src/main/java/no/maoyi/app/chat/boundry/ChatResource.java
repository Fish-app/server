package no.***REMOVED***.app.chat.boundry;

import no.***REMOVED***.app.auth.entity.Group;
import no.***REMOVED***.app.chat.control.ChatService;
import no.***REMOVED***.app.chat.entity.Conversation;
import no.***REMOVED***.app.listing.control.ListingService;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path("chat")
public class ChatResource {

    @Inject
    ChatService service;


    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME,Group.ADMIN_GROUP_NAME})
    @Path("start")
    public Response startConversation(
            @FormDataParam("listing") Long listingId
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

    public("")
}
