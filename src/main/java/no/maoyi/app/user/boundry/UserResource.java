package no.***REMOVED***.app.user.boundry;

import no.***REMOVED***.app.user.control.AuthenticationService;
import no.***REMOVED***.app.user.control.KeyService;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.Seller;
import no.***REMOVED***.app.user.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class UserResource {

    @Inject
    UserService userService;

    /**
     * Returns the current logged in user
     *
     * @return the current loged in user
     */
    @GET
    @Path("current")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response getCurrentUser() {
        ResponseBuilder resp;
        User user = userService.getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(user);
        }
        return resp.build();
    }


    /**
     * Creates a new user in the system.
     *
     * @param name     the first name of the user
     * @param password desired password for the user
     * @param email    email for the user
     *
     * @return returns the user if successful error msg if not
     */
    @POST
    @Path("create")
    public Response createUser(@HeaderParam("name") String name, @HeaderParam("email") String email,
                               @HeaderParam("password") String password
    ) {
        ResponseBuilder resp;
        try {
            email = email.toLowerCase();
            User user = userService.getUserFromEmail(email);
            if (user == null) {
                User newUser = userService.createUser(name, email, password);
                resp = Response.ok(newUser);

            } else {
                resp = Response.ok(
                        "User already exist").status(Response.Status.CONFLICT);
            }

        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user").status(500);
        }
        return resp.build();

    }


}
