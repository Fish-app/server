package no.fishapp.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.listing.entity.Listing;


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

    public static ConversationDTO buildFromConversation(Conversation conversation) {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.id = conversation.getId();
        conversationDTO.lastMessageId = conversation.getLastMessageId();
        conversationDTO.firstMessageId = conversation.getFirstMessageId();
        conversationDTO.listing = conversation.getConversationListing();
        return conversationDTO;
    }

    long id;
    long lastMessageId;
    long firstMessageId;
    Listing listing;

}
