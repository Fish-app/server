package no.fishapp.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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