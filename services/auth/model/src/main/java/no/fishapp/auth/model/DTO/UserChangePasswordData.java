package no.fishapp.auth.model.DTO;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * DTO used to send change password data.
 * Used to facilitate Http POST payload usage for increased security.
 */
@Data
public class UserChangePasswordData {

    /**
     * The old password
     */
    @NotNull
    private String oldPassword;

    /**
     * The new password
     */
    @NotNull
    private String newPassword;

}
