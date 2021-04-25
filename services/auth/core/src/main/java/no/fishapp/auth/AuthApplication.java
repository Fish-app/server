package no.fishapp.auth;


import no.fishapp.auth.model.Group;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/auth-db",
        callerQuery = "select password from auth_users as us where cast(us.id as text)  = ?",
        groupsQuery = "select groups_name from user_groups as ug, auth_users as us where cast(us.id as text) = ? and us.id = ug.user_id",
        priority = 80)
// Roles allowed for authentication
@DeclareRoles({Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.BUYER_GROUP_NAME, Group.ADMIN_GROUP_NAME, Group.CONTAINER_GROUP_NAME})
@ApplicationScoped
@ApplicationPath("/")
public class AuthApplication extends Application {
}
