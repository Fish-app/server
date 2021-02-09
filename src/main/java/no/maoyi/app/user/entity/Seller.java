package no.maoyi.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Date;

@Entity()
@Data
@NoArgsConstructor
@Table(name = "seller")
public class Seller {

    @Id
    private BigInteger id;

    String bankAccountNumber;

    @NotBlank
    @Column(nullable = false)
    String regNumber;

    @OneToOne
    User user;

    public Seller(User user, String regNumber) {
        this.id        = user.getId();
        this.user      = user;
        this.regNumber = regNumber;
    }


}
