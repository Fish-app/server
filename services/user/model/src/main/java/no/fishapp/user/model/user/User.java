package no.fishapp.user.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/* 
Represents a User in the system.
A user has a An ID, email, first name, last name and password.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    @Id
    private long id;

    // -- User info -- //
    @NotBlank
    @Column(nullable = false)
    private String name;


    private String email;


    protected User(long id, String email, String name) {
        this.setId(id);
        this.setEmail(email);
        this.setName(name);
    }


}