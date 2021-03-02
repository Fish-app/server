package no.fishapp.app.auth.entity.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


/**
 * increese security by not having the login data in headers or uri
 */
@Data
public class UserLoginData {

    @NotNull
    String password;

    @NotNull
    @Email
    String userName;
}
