package no.fishapp.auth.entity;

import lombok.Data;

import javax.persistence.*;

/*
	Represents a user group in the application. A user group is
	used for permissions, where each group has different permissions when 
	accessing resources. 
*/
@Data
@Entity
@Table(name = "group_names")
public class Group {

    public static final String BUYER_GROUP_NAME = "buyer";
    public static final String USER_GROUP_NAME = "user";
    public static final String SELLER_GROUP_NAME = "seller";
    public static final String ADMIN_GROUP_NAME = "admin";
    public static final String[] GROUPS = {USER_GROUP_NAME, BUYER_GROUP_NAME, SELLER_GROUP_NAME, ADMIN_GROUP_NAME};

    @Id
    String name;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

}