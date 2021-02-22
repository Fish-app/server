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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    public ConversationDTO(Conversation conversation) {
        this.id        = conversation.getId();
        this.baseOrder = conversation.getBaseOrder();
        this.messageCount = conversation.messages.size();
        this.messages  = conversation.messages.stream()
                                              .parallel()
                                              .map(MessageDTO::buildFromMessage)
                                              .collect(Collectors.toList());
    }

    long id;

    Listing baseOrder;

    @JsonbTransient
    List<MessageDTO> messages;

    int messageCount;

}
