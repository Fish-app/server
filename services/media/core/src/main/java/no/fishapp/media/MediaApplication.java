package no.fishapp.media;


import no.fishapp.auth.model.Group;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


// Roles allowed for authentication
@DeclareRoles({Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.BUYER_GROUP_NAME, Group.ADMIN_GROUP_NAME, Group.CONTAINER_GROUP_NAME})
@ApplicationPath("/")
public class MediaApplication extends Application {
}
