package no.fishapp.auth.entity.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Locale;


/**
 * increese security by not having the login data in headers or uri
 */
public class UsernamePasswordData {

    @NotNull
    String password;

    @NotNull
    @Email
    String userName;

    public UsernamePasswordData() {
    }

    public UsernamePasswordData(
            @NotNull String password,
            @NotNull @Email String userName) {
        this.password = password;
        this.userName = userName.toLowerCase(Locale.ROOT);
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName.toLowerCase(Locale.ROOT);
    }
}
