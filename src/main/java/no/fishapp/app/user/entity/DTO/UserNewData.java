package no.fishapp.app.user.entity.DTO;

import lombok.Data;
import no.fishapp.app.auth.entity.DTO.UserLoginData;
import no.fishapp.app.user.entity.User;

@Data
public class UserNewData extends UserLoginData {
    String name;
}
