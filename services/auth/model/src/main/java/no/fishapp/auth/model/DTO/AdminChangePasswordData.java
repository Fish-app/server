package no.fishapp.auth.model.DTO;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * increese security by not having the login data in headers or uri
 */
@Data
public class AdminChangePasswordData {

    @NotNull
    Long userId;

    @NotNull
    String newPassword;
}
