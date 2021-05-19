package no.fishapp.media.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Represents a storable image.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "images")
public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    //The id of the image
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    //The filename of the image
    @NotEmpty
    private String name;

    //The mimetype of the image
    @NotEmpty
    @Column(name = "mime_type")
    private String mimeType;

    //The size of the image
    @Min(0)
    private int size;


}
