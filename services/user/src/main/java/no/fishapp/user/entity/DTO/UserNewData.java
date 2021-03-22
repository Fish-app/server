package no.fishapp.user.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.entity.DTO.UsernamePasswordData;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNewData extends UsernamePasswordData {
    String name;
}
