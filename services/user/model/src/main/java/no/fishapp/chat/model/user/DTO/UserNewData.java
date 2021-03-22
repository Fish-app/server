package no.fishapp.chat.model.user.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.DTO.UsernamePasswordData;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNewData extends UsernamePasswordData {
    String name;
}
