package no.fishapp.app.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.app.auth.entity.DTO.UserLoginData;
import no.fishapp.app.user.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNewData extends UserLoginData {
    String name;
}
