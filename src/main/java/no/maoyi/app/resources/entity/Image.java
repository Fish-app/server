package no.***REMOVED***.app.resources.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Represents a storeable image.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "images")
public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @NotEmpty
    private String name;

    @NotEmpty
    @Column(name = "mime_type")
    private String mimeType;

    @Min(0)
    private float size;


}
