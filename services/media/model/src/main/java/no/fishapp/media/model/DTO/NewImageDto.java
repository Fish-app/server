package no.fishapp.media.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.InputStream;


/**
 * This DTO is used for saving an {@link no.fishapp.media.model.Image}.
 */
@Data
@NoArgsConstructor
public class NewImageDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private String mimeType;

    @Min(0)
    private int size;

    private InputStream imageDataStream;
}
