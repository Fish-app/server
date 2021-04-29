package no.fishapp.chat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * The entity for a Message. Stored in the SQL database.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    // TODO: add images to the messages in future

    /**
     * The identifier of the message.
     * Used as PK in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * The content of the message. Content is set by
     * reading a {@code MessageBody} sent from clients.
     */
    @Column(columnDefinition = "TEXT")
    String content;

    /**
     * The ID of the user sending this message.
     */
    long senderId;

    /**
     * A timestamp in UNIX Epoch when
     * the conversation was created
     */
    @Column(name = "created_date")
    Long createdDate;

    /**
     * Retrieves the system time, saves it
     * as a timestamp before writing the
     * conversation to the database.
     */
    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }

}
