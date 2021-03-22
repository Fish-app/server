package no.fishapp.chat.model.user;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "buyers")
@EqualsAndHashCode(callSuper = true)
public class Buyer extends User {

    public Buyer(long id, String email, String name) {
        super(id, email, name);
    }
}
