package no.fishapp.auth.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.Group;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private String userName;

    /**
     * the new users password
     */
    @NotNull
    private String password;

    /**
     * A list of the names of the {@link Group} the user should be a member of.
     */
    @NotNull
    private List<String> groups;
}
