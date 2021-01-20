package no.maoyi.app.startup;

import no.maoyi.app.user.entity.Group;
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.annotation.sql.DataSourceDefinition;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
/* 
	Sets application security settings / configurations
*/

@DataSourceDefinition(
        name = "java:global/jdbc/DemoDataSource",
        className       = "org.postgresql.ds.PGSimpleDataSource",
        serverName      = "${MPCONFIG=dataSource.serverName}",
        portNumber      = 5432,
        databaseName    = "${MPCONFIG=dataSource.databaseName}",
        user            = "${MPCONFIG=dataSource.user}",
        password        = "${MPCONFIG=dataSource.password}",
        minPoolSize     = 10,
        maxPoolSize     = 50
)
// Adds credential validation queries to validation store.
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:global/jdbc/DemoDataSource",
        callerQuery = "select password from users as us where cast(us.id as text)  = ?",
        groupsQuery = "select groups_name from user_groups as ug, users as us where cast(us.id as text) = ? and us.id = ug.user_id",
        priority = 80)

// Roles allowed for authentication
@DeclareRoles({Group.USER_GROUP_NAME, Group.USER_GROUP_NAME})

// Sets authentication to JWT, using recipe-heaven issuer
@LoginConfig(authMethod = "MP-JWT", realmName = "maoyi")
public class SecurityConfiguration {
}