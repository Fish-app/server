package no.***REMOVED***.app.user.control;

import no.***REMOVED***.app.user.entity.Group;
import no.***REMOVED***.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.*;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.math.BigInteger;

@Transactional
public class AuthenticationService {

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @PersistenceContext
    EntityManager entityManager;


    @Inject
    PasswordHash hasher;

    @Inject
    UserService userService;


    /**
     * Util method checks if the auth {@link CredentialValidationResult} result is valid
     *
     * @param result
     *
     * @return
     */
    public boolean isAuthValid(CredentialValidationResult result) {
        return result.getStatus() == CredentialValidationResult.Status.VALID;
    }

    /**
     * Util method, gets the current {@link CredentialValidationResult}
     *
     * @param userId   Users id
     * @param password Users password
     *
     * @return the credential val result for this username pass combo
     */
    public CredentialValidationResult getValidationResult(BigInteger userId, String password) {
        return identityStoreHandler.validate(new UsernamePasswordCredential(String.valueOf(userId), password));
    }

    /**
     * Changes the password for the logged inn user.
     *
     * @param newPassword the new password to set.
     */
    public void changePassword(String newPassword) {
        User user = userService.getLoggedInUser();
        user.setPassword(hasher.generate(newPassword.toCharArray()));
        entityManager.merge(user);
    }

    public Response validatePasswordChangeRequest(
            String emailAccess,
            String newPasswd,
            String oldPasswd,
            SecurityContext sc
    ) {
        if (emailAccess == null || newPasswd == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        emailAccess = emailAccess.toLowerCase();
        StringBuilder logMesg = new StringBuilder();
        logMesg.append("AUTH: Change password for '" + emailAccess + "'");

        User accessUser = userService.getUserFromEmail(emailAccess);
        if (accessUser == null) {
            logMesg.append(" - FAIL (User not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        BigInteger id = accessUser.getId();
        Boolean authorizedToChange = false;

        logMesg.append(" - REQUEST BY (" + id + ", " + accessUser.getEmail() + ") ");


        // The user initiating the password change request (aka the caller)
        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;

        Response.Status state = Response.Status.BAD_REQUEST;

        if ((newPasswd == null || newPasswd.length() < 6)) {
            logMesg.append(" - FAIL(Password unsatisfied)");
        } else {
            // Admin rolegroup has permission to change password for other users (and overrides the checks below)
            if (sc.isUserInRole(Group.ADMIN_GROUP_NAME)) {
                state = Response.Status.OK;
                authorizedToChange = true;

                // 1. Verify caller has RoleGroup.USER
                // 2. Verify caller IS SAME user as will change password to (Zero difference in strings (== 0))
                // 3, Verify that caller has entered old password (admins shall never req usr old passwd!)
            } else if (sc.isUserInRole(Group.USER_GROUP_NAME) && authuser.compareToIgnoreCase(id.toString()) == 0  && oldPasswd != null) {
                try {
                    CredentialValidationResult result = getValidationResult(id, oldPasswd);

                    switch (result.getStatus()) {
                        case VALID:
                            authorizedToChange = true;
                            state = Response.Status.OK;
                            logMesg.append("OK");
                            break;

                        case INVALID:
                            state = Response.Status.FORBIDDEN;
                            logMesg.append("FORBIDDEN");
                            break;

                        case NOT_VALIDATED:
                            // The server was unable to validate the credentials,
                            // thus a failure code is returned
                            state = Response.Status.SERVICE_UNAVAILABLE;
                            logMesg.append("VALIDATION FAILED");
                            break;
                    }
                } catch (Exception e) {
                    // The server entered an exception state
                    state = Response.Status.INTERNAL_SERVER_ERROR;
                    logMesg.append("SERVER EXCEPTION");

                }
            } else {
                state = Response.Status.UNAUTHORIZED;
                logMesg.append("UNAUTHORIZED");
            }
        }

        if (authorizedToChange) {
            changePassword(newPasswd);
            state = Response.Status.fromStatusCode(200);
            logMesg.append(" -> Change successful");
        } else {
            logMesg.append(" -> Change aborted");
        }

        System.out.println(logMesg);
        return Response.status(state).build();
    }

}
