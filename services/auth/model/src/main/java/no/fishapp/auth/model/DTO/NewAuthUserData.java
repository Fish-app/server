package no.fishapp.auth.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.Group;

import java.util.List;


/**
 * {@code NewAuthUserData} is a DTO used to communicate the creation of new {@link no.fishapp.auth.model.AuthenticatedUser} between fishapp micro-services
 */
@Data
@NoArgsConstructor
public class NewAuthUserData {

    /**
     * The new users username
     */
    String userName;

    /**
     * the new users password
     */
    String password;

    /**
     * A list of the names of the {@link Group} the user should be a member of.
     */
    List<String> groups;
}
