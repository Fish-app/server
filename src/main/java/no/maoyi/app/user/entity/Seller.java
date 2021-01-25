package no.maoyi.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "sellers")
@EqualsAndHashCode(callSuper = true)
@NamedQuery(name = Seller.SELLER_BY_EMAIL, query = "SELECT e FROM Seller e WHERE e.email = :email")
public class Seller extends User{

    public static final String SELLER_BY_EMAIL = "Seller.getByEmail";

    String bankAccountNumber;
    String regNumber;

    public Seller (String name, String username, String email, String password, String bankAccountNumber, String regNumber) {
        super(name, username, email, password);
        this.setBankAccountNumber(bankAccountNumber);
        this.setRegNumber(regNumber);
    }
}
