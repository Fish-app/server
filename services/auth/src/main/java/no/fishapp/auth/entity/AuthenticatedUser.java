package no.fishapp.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "auth_users")
public class AuthenticatedUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date created;


    @Size(min = 6)
    @JsonbTransient
    @Column(nullable = false)
    private String password;

    @JsonbTransient
    @Column(nullable = false, unique = true)
    private String principalName;

    @ManyToMany
    @JsonbTransient
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "groups_name", referencedColumnName = "name"))
    List<Group> groups;

    public AuthenticatedUser(@Size(min = 6) String password, String principalName) {
        this.password = password;
        this.principalName = principalName;
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
