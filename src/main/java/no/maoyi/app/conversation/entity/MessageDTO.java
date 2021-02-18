package no.***REMOVED***.app.conversation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.user.entity.User;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    public static MessageDTO buildFromMessage(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.id       = message.getId();
        messageDTO.content  = message.content;
        messageDTO.senderId = message.sender.getId();
        return messageDTO;
    }

    long id;

    String content;

    long senderId;
}
