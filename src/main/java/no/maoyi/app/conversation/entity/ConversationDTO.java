package no.maoyi.app.conversation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.listing.entity.Listing;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This and the message DTO exists so the message backlog does not have to include the
 * user info of the sender once per message when all that's needed is the user id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    public ConversationDTO(Conversation conversation) {
        this.id        = conversation.getId();
        this.baseOrder = conversation.getBaseOrder();
        this.messages  = conversation.messages.stream()
                                              .parallel()
                                              .map(MessageDTO::buildFromMessage)
                                              .collect(Collectors.toList());
    }

    long id;

    Listing baseOrder;

    List<MessageDTO> messages;


}
