package no.fishapp.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents the differen groops used for authentication in the application.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "group_names")
public class Group implements Serializable {

    public static final String BUYER_GROUP_NAME = "buyer";
    public static final String USER_GROUP_NAME = "user";
    public static final String SELLER_GROUP_NAME = "seller";
    public static final String ADMIN_GROUP_NAME = "admin";
    public static final String CONTAINER_GROUP_NAME = "container";

    public static final String[] GROUPS = {CONTAINER_GROUP_NAME, USER_GROUP_NAME, BUYER_GROUP_NAME, SELLER_GROUP_NAME, ADMIN_GROUP_NAME};

    /**
     * The group name.
     */
    @Id
    String name;

    public Group(String name) {
        this.name = name;
    }

    public static boolean isValidGroupName(String name) {
        return Arrays.asList(GROUPS).contains(name);
    }

}