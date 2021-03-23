package no.fishapp.user.boundry;


import no.fishapp.auth.model.Group;
import no.fishapp.chat.model.user.DTO.SellerNewData;
import no.fishapp.chat.model.user.Seller;
import no.fishapp.user.control.SellerService;
import no.fishapp.user.exception.UsernameAlreadyInUseException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("seller")
@Transactional
@RolesAllowed(value = {Group.SELLER_GROUP_NAME})
public class SellerResource {

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
     * @return the seller if creation is ok error if not
     */
    @POST
    @Path("create")
    @PermitAll
    public Response createSeller(
            SellerNewData sellerNewData
    ) {
        Response.ResponseBuilder resp;
        try {
            var newSeller = sellerService.createSeller(sellerNewData);
            resp = Response.ok(newSeller);

        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user").status(500);
        } catch (UsernameAlreadyInUseException e) {
            resp = Response.ok(
                    "User already exist").status(Response.Status.CONFLICT);
        }
        return resp.build();
    }
}
