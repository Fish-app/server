package no.fishapp.chat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * This and the {@link MessageDTO} exists so the message backlog does not have to include the
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
     * Builds a {@link ConversationDTO}
     *
     * @param conversation the conversation to use for this DTO
     * @return a Conversation DTO
     */
    public static ConversationDTO buildFromConversation(Conversation conversation) {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.id            = conversation.getId();
        conversationDTO.lastMessageId = conversation.getLastMessageId();
        conversationDTO.listingId     = conversation.getListingId();
        conversationDTO.starterUserId = conversation.getConversationStarterUserId();
        conversationDTO.createdDate   = conversation.getCreatedDate();
        return conversationDTO;
    }

    /**
     * Builds a {@link ConversationDTO} and attach the last message as a {@code MessageDTO}
     *
     * @param conversation the conversation to use for this DTO
     * @param message      the last message to include in the conversation.
     * @return a {@code ConversationDTO}
     */
    public static ConversationDTO buildFromConversation(Conversation conversation, Message message) {
        ConversationDTO convDto = buildFromConversation(conversation);
        convDto.lastMessage = MessageDTO.buildFromMessage(message);
        return convDto;
    }

    /**
     * These variables holds the same data as the {@link Conversation} class
     */
    long id;
    long lastMessageId;
    long createdDate;
    long starterUserId;
    long listingId;
    MessageDTO lastMessage;
}
