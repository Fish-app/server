package no.***REMOVED***.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;


@Data
@Entity
@NoArgsConstructor
@Table(name = "sellers")
@EqualsAndHashCode(callSuper = true)
public class Seller extends User {

    String bankAccountNumber;

    @NotBlank
    @Column(nullable = false)
    String regNumber;


    public Seller(String email, String name, String password, String regNumber) {
        super(email, name, password);
        this.regNumber = regNumber;
    }


}
