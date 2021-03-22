package no.fishapp.auth;


import no.fishapp.auth.entity.Group;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.DeclareRoles;
import javax.annotation.sql.DataSourceDefinition;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


//@DataSourceDefinition(
//        name = "jdbc/auth-db",
//        className = "org.postgresql.ds.PGSimpleDataSource",
//        serverName = "${MPCONFIG=dataSource.serverName}",
//        portNumber = 33333,
//        databaseName = "${MPCONFIG=dataSource.databaseName}",
//        user = "${MPCONFIG=dataSource.user}",
//        password = "${MPCONFIG=dataSource.password}",
//        minPoolSize = 10,
//        maxPoolSize = 50
//)
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/auth-db",
        callerQuery = "select password from auth_users as us where cast(us.id as text)  = ?",
        groupsQuery = "select groups_name from user_groups as ug, auth_users as us where cast(us.id as text) = ? and us.id = ug.user_id",
        priority = 80)
// Roles allowed for authentication
@DeclareRoles({Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.BUYER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
@ApplicationPath("/")
public class AuthApplication extends Application {

    //    AuthApplication() {
    //        register(MultiPartFeature.class);
    //        packages(true, "no.fishapp.auth");
    //    }

}
