package no.fishapp.media.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@Data
@NoArgsConstructor
public class NewImageDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private String mimeType;

    @Min(0)
    private int size;
}
