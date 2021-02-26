package no.***REMOVED***.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.listing.entity.Listing;
import no.***REMOVED***.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This and the message DTO exists so the message backlog does not have to include the
 * user object of the sender once per message, when all that's needed is the user id
 * <p>
 * Returns metadata about a conversation; what order asscoiated, the current count of msgs
 * so that the client can send an GET message list seperatly with only the messages it want to know
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    public ConversationDTO(Conversation conversation) {
        this.id = conversation.getId();
        this.lastMessageId = conversation.getLastMessageId();
        this.firstMessageId = conversation.getFirstMessageId();
        this.listing = conversation.getConversationListing();
    }

    long id;
    long lastMessageId;
    long firstMessageId;
    Listing listing;

}
