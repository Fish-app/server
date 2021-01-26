package no.***REMOVED***.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@NoArgsConstructor
@Table(name = "sellers")
@EqualsAndHashCode(callSuper = true)
@NamedQuery(name = Seller.SELLER_BY_EMAIL, query = "SELECT e FROM Seller e WHERE e.email = :email")
public class Seller extends User{

    public static final String SELLER_BY_EMAIL = "Seller.getByEmail";

    @NotBlank
    @Column(nullable = false)
    String bankAccountNumber;

    @NotBlank
    @Column(nullable = false)
    String regNumber;

    public Seller (String name, String email, String password, String bankAccountNumber, String regNumber) {
        super(name, email, password);
        this.setBankAccountNumber(bankAccountNumber);
        this.setRegNumber(regNumber);
    }
}
