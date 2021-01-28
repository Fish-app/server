package no.maoyi.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity()
@Data
@NoArgsConstructor
@Table(name = "seller")
@EqualsAndHashCode(callSuper = true)
@NamedQuery(name = Seller.SELLER_BY_EMAIL, query = "SELECT e FROM Seller e WHERE e.email = :email")
public class Seller extends User{

    public static final String SELLER_BY_EMAIL = "Seller.getByEmail";

    String bankAccountNumber;

    @NotBlank
    @Column(nullable = false)
    String regNumber;

    public Seller (String name, String email, String password, String regNumber) {
        super(email, name, password);
        System.out.println("----------------------------------------------------------------------------------------");
        this.setRegNumber(regNumber);
    }


}
