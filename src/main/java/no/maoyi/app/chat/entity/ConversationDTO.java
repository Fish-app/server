package no.maoyi.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.listing.entity.Listing;

import javax.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This and the message DTO exists so the message backlog does not have to include the
 * user object of the sender once per message, when all that's needed is the user id
 *
 *  Returns metadata about a conversation; what order asscoiated, the current count of msgs
 *  so that the client can send an GET message list seperatly with only the messages it want to know
 *  FIXME: messages should be sent in a seperate DTO/ JSON list ??
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    public ConversationDTO(Conversation conversation) {
        this.id        = conversation.getId();
        this.baseOrder = conversation.getBaseOrder();
        this.version = conversation.getVersion();
        this.messageCount = conversation.messages.size();
        this.messages  = conversation.messages.stream()
                                              .parallel()
                                              .map(MessageDTO::buildFromMessage)
                                              .collect(Collectors.toList());
    }

    long id;

    long version;

    Listing baseOrder;

    // FIXME: better to use message list in a seperate endpoint ?
    @JsonbTransient
    List<MessageDTO> messages;

    int messageCount;

}
