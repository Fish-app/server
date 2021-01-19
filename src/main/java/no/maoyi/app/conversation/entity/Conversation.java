package no.maoyi.app.conversation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.order.entity.BaseOrder;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @ManyToOne(cascade = CascadeType.ALL)
    BaseOrder baseOrder;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Message> messages;

    @Column(columnDefinition = "TEXT")
    String title;

    @Column(columnDefinition = "TEXT")
    String description;


}
