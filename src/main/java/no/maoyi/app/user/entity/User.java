package no.maoyi.app.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.maoyi.app.conversation.entity.Conversation;
import no.maoyi.app.listing.entity.Listing;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* 
Represents a User in the system.
A user has a An ID, email, first name, last name and password.
 */
@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@NamedQuery(name = User.USER_BY_EMAIL, query = "SELECT e FROM User e WHERE e.email = :email")
public class User implements Serializable {

    public static final String USER_BY_EMAIL = "User.getByEmail";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    // -- User info -- //
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 6)
    @JsonbTransient
    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JsonbTransient
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "groups_name", referencedColumnName = "name"))
    List<Group> groups;


    // -- User data -- //

    @OneToMany
    @JsonbTransient
    List<Conversation> userConversations;


    @OneToMany
    @JsonbTransient
    List<Listing> userCreatedOrders;


    public User(String email, String name, String password) {
        System.out.println("----------------------------------------------------------------------------------------");
        this.setEmail(email);
        this.setName(name);
        this.setPassword(password);
    }

    public List<Group> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }


    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

}