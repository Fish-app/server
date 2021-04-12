package no.fishapp.chat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    // TODO: add images to the messages in future

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(columnDefinition = "TEXT")
    String content;

    long senderId;

    @Column(name = "created_date")
    Long createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }

}
