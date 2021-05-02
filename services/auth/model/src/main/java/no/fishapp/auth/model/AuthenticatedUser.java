package no.fishapp.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Represents an authenticated user in the system. Describes how this user authenticate themselves
 * and what kind of permissions the user has.
 */
@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "auth_users")
public class AuthenticatedUser implements Serializable {


    /**
     * The users id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The users hashed and salted password.
     */
    @Size(min = 6)
    @JsonbTransient
    @Column(nullable = false)
    private String password;

    /**
     * The users principal name. usually email
     */
    @Column(nullable = false, unique = true)
    private String principalName;

    /**
     * The auth groups the user is a member of
     */
    @ManyToMany
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "groups_name", referencedColumnName = "name"))
    List<Group> groups;

    /**
     * The date the user where created.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date created;

    /**
     * Creates a new {@code AuthenticatedUser}
     *
     * @param password      The hashed and salted user pasword
     * @param principalName the user principal name
     */
    public AuthenticatedUser(@Size(min = 6) String password, String principalName) {
        this.password = password;
        this.principalName = principalName;
    }

    /**
     * return the groups the user is a member of.
     *
     * @return the groups the user is a member of.
     */
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
