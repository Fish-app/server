package no.fishapp.chat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  This MessageDTO represents a {@link Message} when limited
 *  data exposure is wanted. It is used when returning messages
 *  to a client from the REST API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    public static MessageDTO buildFromMessage(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.id = message.getId();
        messageDTO.content = message.getContent();
        messageDTO.createdDate = message.getCreatedDate();
        messageDTO.senderId = message.senderId;
        return messageDTO;
    }

    /**
     * These variables holds the same data as the {@link Conversation} class
     */
    long id;
    long createdDate;
    String content;
    long senderId;
}
