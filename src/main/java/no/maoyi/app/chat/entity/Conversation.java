package no.***REMOVED***.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.***REMOVED***.app.listing.entity.Listing;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    Listing baseOrder;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Message> messages;

    @Column(columnDefinition = "TEXT")
    String title;

    @Column(columnDefinition = "TEXT")
    String description;


    public Conversation(Listing listing) {
        this.baseOrder = listing;

    }
}
