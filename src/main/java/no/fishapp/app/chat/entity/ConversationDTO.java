package no.fishapp.app.chat.entity;


import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.chat.control.ChatService;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.user.entity.User;

import javax.transaction.Transactional;


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

    /**
     * Builds a conversation DTO
     *
     * @param conversation the conversation to use for this DTO
     *
     * @return a Conversation DTO
     */
    public static ConversationDTO buildFromConversation(Conversation conversation) {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.id            = conversation.getId();
        conversationDTO.lastMessageId = conversation.getLastMessageId();
        conversationDTO.listing       = conversation.getConversationListing();
        conversationDTO.starterUser   = conversation.getConversationStarterUser();
        return conversationDTO;
    }


    /**
     * Builds a conversation DTO and attach the last message as a MessageDTO
     *
     * @param conversation the conversation to use for this DTO
     * @param message      the last message to include in the conversation.
     *
     * @return a Conversation DTO
     */
    public static ConversationDTO buildFromConversation(Conversation conversation, Message message) {
        ConversationDTO convDto = buildFromConversation(conversation);
        convDto.lastMessage = MessageDTO.buildFromMessage(message);
        return convDto;
    }


    long id;
    long lastMessageId;
    long createdDate;
    User starterUser;
    Listing listing;
    MessageDTO lastMessage;

}
