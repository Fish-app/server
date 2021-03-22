package no.fishapp.store.core.listing.boundry;


import no.fishapp.app.auth.control.KeyService;
import no.fishapp.app.auth.entity.Group;
import no.fishapp.store.core.listing.control.ListingService;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(ListingResource.class.getName()).log(Level.SEVERE, "presist exept", e);
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
            Logger.getLogger(ListingResource.class.getName()).log(Level.SEVERE, "presist exept", e);
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resp.build();
    }

    @GET
    @Path("{id}")
    public Response getOfferListing(
            @NotNull @PathParam("id") Long id
    ) {
        OfferListing result = listingService.findOfferListingById(id);
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(result).build();
        }
    }

    @GET
    @Path("buyrequest/{id}")
    public Response getBuyRequest(
            @NotNull @PathParam("id") Long id
    ) {
        BuyRequest result = listingService.findBuyRequestById(id);
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(result).build();
        }
    }


    @GET
    @Path("comodity/{id}")
    public Response getCommodityOfferListings(
            @PathParam("id") long id
    ) {
        return Response.ok(listingService.getCommodityOfferListings(id)).build();
    }
}
