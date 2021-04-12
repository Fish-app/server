package no.fishapp.store.listing.boundary;


import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.listing.control.ListingService;
import no.fishapp.store.model.listing.BuyRequest;
import no.fishapp.store.model.listing.OfferListing;
import org.eclipse.microprofile.jwt.Claim;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("listing")
@Log
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
            log.log(Level.SEVERE, "presist exept", e);
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
            log.log(Level.SEVERE, "presist exept", e);
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
