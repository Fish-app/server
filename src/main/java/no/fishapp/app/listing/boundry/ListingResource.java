package no.fishapp.app.listing.boundry;


import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.listing.control.ListingService;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("listing")
public class ListingResource {

    @Inject
    ListingService listingService;

    /**
     * Server endpoint for creating a new offer listing
     *
     * @return return the offer listing if successful, error msg if not
     */
    @POST
    @Path("newOfferListing")
    @RolesAllowed(value = {Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newOfferListing(OfferListing newOfferListing
    ) {
        Response.ResponseBuilder resp;

        try {
            OfferListing offerListing = listingService.newOfferListing(newOfferListing);
            resp = Response.ok(offerListing);
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resp.build();
    }

    /**
     * Server endpoint for creating a new buy request
     *
     * @return the buy request object if successful, error msg if not
     */
    @POST
    @Path("newBuyRequest")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newBuyRequest(
            BuyRequest newBuyRequest
    ) {
        Response.ResponseBuilder resp;

        try {
            BuyRequest buyRequest = listingService.newBuyRequest(newBuyRequest);
            resp = Response.ok(buyRequest);
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resp.build();
    }
}
