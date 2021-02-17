package no.maoyi.app.user.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity()
@NoArgsConstructor
@Table(name = "buyers")
@EqualsAndHashCode(callSuper = true)
public class Buyer extends User {

    public Buyer(String email, String name, String password) {
        super(email, name, password);
    }
}
