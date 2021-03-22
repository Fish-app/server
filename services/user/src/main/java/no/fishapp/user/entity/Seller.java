package no.fishapp.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


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


    public Seller(long id, String name, String email, String regNumber) {
        super(id, email, name);
        this.regNumber = regNumber;
    }


}
