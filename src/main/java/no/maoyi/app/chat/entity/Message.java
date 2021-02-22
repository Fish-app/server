package no.maoyi.app.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.maoyi.app.resources.entity.Image;
import no.maoyi.app.user.entity.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    // TODO: May add images to the messages

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    //TODO: Get Payload as seperate type/enum to seperate between message types (textmessage, imagemessage etc.)
    @Column(columnDefinition = "TEXT")
    String content;
    //MessagePayload content;



    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    Image image;

    @ManyToOne
    User sender;

    @Column(name = "created_date")
    Long createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date().getTime(); // Get epoch time
    }

    //N-1 Owner
    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    @JsonbTransient
    private Conversation conversation;


    public Message(String content, User sender) {
        this.content = content;
        this.sender = sender;
    }
}
