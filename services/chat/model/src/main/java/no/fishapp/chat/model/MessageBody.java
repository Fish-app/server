package no.fishapp.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * The MessageBody is used by clients
 * to store content when sending a new {@link Message}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBody {
    @Getter
    @NotNull
    String messageText;
}
