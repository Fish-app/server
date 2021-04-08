package no.fishapp.auth.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.Group;

import java.util.List;

@Data
@NoArgsConstructor
public class NewAuthUserData {
    String userName;
    String password;
    List<String> groups;
}
