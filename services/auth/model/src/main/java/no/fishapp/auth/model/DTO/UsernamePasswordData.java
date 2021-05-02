package no.fishapp.auth.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Locale;


/**
 * Data transfer objet used to provide loggin data.
 * Used to facilitate Http POST payload usage for increased security.
 */
@Data
@NoArgsConstructor
public class UsernamePasswordData {

    /**
     * The users login password
     */
    @NotNull
    private String password;

    /**
     * The users login user name
     */
    @NotNull
    @Email
    private String userName;

    /**
     * Creates a new {@code UsernamePasswordData}.
     * Sets all the username characters to lower case to avoid email troubles
     *
     * @param password The users password
     * @param userName The users username
     */
    public UsernamePasswordData(
            @NotNull String password, @NotNull @Email String userName) {
        this.password = password;
        this.userName = userName.toLowerCase(Locale.ROOT);
    }


    /**
     * Sets the users username. All character are set to lower case
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName.toLowerCase(Locale.ROOT);
    }
}
