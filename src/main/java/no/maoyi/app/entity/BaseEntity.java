package no.***REMOVED***.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;



@Data
@NoArgsConstructor
public class BaseEntity {
    /**
     * The id of the entity
     */

    @Id
    BigInteger id;
}
