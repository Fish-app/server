package no.maoyi.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.user.entity.User;

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
 * FIXME: messages should be sent in a seperate DTO/ JSON list ??
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    public ConversationDTO(Conversation conversation) {
        this.id = conversation.getId();
        this.messageCount = conversation.messages.size();
        this.lastMessageId = conversation.getLastMessageId();
        this.lastSenderId = conversation.get
    }

    long id;

    long version;


    int messageCount;

    long lastMessageId;

}
