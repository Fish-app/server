package no.fishapp.app.user.boundry;

import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.user.control.SellerService;
import no.fishapp.app.auth.control.AuthenticationService;
import no.fishapp.app.user.entity.Seller;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("seller")
@Transactional
@RolesAllowed(value = {Group.SELLER_GROUP_NAME})
public class SellerResource {

    @Inject
    AuthenticationService authenticationService;

    @Inject
    SellerService sellerService;

    /**
     * Returns the currently logged in seller, if not a seller a error is returned.
     *
     * @return the current seller object.
     */
    @GET
    @Path("current")
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

    /**
     * Makes the current user a seller
     *
     * @param regNumber TODO: find actual params
     *
     * @return the seller if creation is ok error if not
     */
    @POST
    @Path("create")
    @PermitAll
    public Response createSeller(@HeaderParam("name") String name,
                                 @HeaderParam("email") String email,
                                 @HeaderParam("password") String password,
                                 @HeaderParam("regNumber") String regNumber
    ) {
        Response.ResponseBuilder resp;
        try {
            email = email.toLowerCase();
            boolean isPrincipalInUse = authenticationService.isPrincipalInUse(email);
            if (!isPrincipalInUse) {
                Seller seller = sellerService.createSeller(name, email, password, regNumber);
                resp = Response.ok(seller);
        } else {
            resp = Response.ok(
                    "User already exist").status(Response.Status.CONFLICT);
        }
    } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the seller").status(Response.Status.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return resp.build();
    }
}
