package no.maoyi.app.user.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.maoyi.app.auth.entity.AuthenticatedUser;
import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.listing.entity.Listing;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/* 
Represents a User in the system.
A user has a An ID, email, first name, last name and password.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true, exclude = "userConversations")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AuthenticatedUser implements Serializable {


    // -- User info -- //
    @NotBlank
    @Column(nullable = false)
    private String name;

    //    @Email
    //    @Column(nullable = false, unique = true)
    //    private String email;

    @Transient
    private String email;

    // -- User data -- //

    // The conversations this user has participated in
    // Intended to be used for fetching the current users conversations
    // M-N OWNER
    @ManyToMany
    @JoinTable(
            name = "users_has_conversations",
            joinColumns =
                @JoinColumn(name = "user_id"),
            inverseJoinColumns =
                @JoinColumn(name = "conversation_id")
    )
    @JsonbTransient
    List<Conversation> userConversations;


    @OneToMany
    @JsonbTransient
    List<Listing> userCreatedOrders;

    ////////////////////////////////////////
    // todo: unshure if horrible design

    public void setEmail(String email) {
        this.email = email;
        this.setPrincipalName(email);
    }

    @PostLoad
    private void setMailToPrincipal() {
        this.email = this.getPrincipalName();
    }

    ///////////////////////////////////////


    protected User(String email, String name, String password) {
        this.setEmail(email);
        this.setName(name);
        this.setPassword(password);
    }
}