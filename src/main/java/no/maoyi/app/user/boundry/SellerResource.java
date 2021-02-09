package no.***REMOVED***.app.user.boundry;

import no.***REMOVED***.app.user.control.AuthenticationService;
import no.***REMOVED***.app.user.control.KeyService;
import no.***REMOVED***.app.user.control.SellerService;
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
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("seller")
@Transactional
public class SellerResource {

    @Inject
    SellerService sellerService;

    /**
     * Makes the current user a seller
     *
     * @param regNumber TODO: find actual params
     *
     * @return the seller if creation is ok error if not
     */
    @POST
    @Path("create")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME})
    public Response createSeller(@HeaderParam("regNumber") String regNumber
    ) {
        Response.ResponseBuilder resp;
        try {
            Seller seller = sellerService.createSeller(regNumber);
            resp = Response.ok(seller);
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the seller").status(Response.Status.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return resp.build();
    }

    /**
     * Returns the currently logged in seller, if not a seller a error is returned.
     *
     * @return the current seller object.
     */
    @GET
    @Path("current")
    @RolesAllowed(value = {Group.SELLER_GROUP_NAME})
    public Response getSeller() {
        Response.ResponseBuilder resp;
        Seller                   seller = sellerService.getLoggedInSeller();
        if (seller == null) {
            resp = Response.ok("Could not find seller").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(seller);
        }
        return resp.build();
    }
}
