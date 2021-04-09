package no.fishapp.chat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    long id;

    long createdDate;

    String content;

    long senderId;
}
