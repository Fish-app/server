package no.fishapp.store.listing.boundary;


import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.listing.control.ListingService;
import no.fishapp.store.model.listing.BuyRequest;
import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.store.model.listing.OfferListing;
import org.eclipse.microprofile.jwt.Claim;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("listing")
@Log
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ListingResource {

    @Inject
    ListingService listingService;

    /**
     * Server endpoint for creating a new offer listing
     *
     * @return return the offer listing if successful, error msg if not
     */
    @POST
    @Path("offer/new")
    @RolesAllowed(value = {Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newOfferListing(
            OfferListing newOfferListing
    ) {
        Optional<OfferListing> offerListing = listingService.newOfferListing(newOfferListing);

        return offerListing.map(Response::ok)
                           .orElse(Response.ok("Unexpected error creating the offer listing")
                                           .status(Response.Status.INTERNAL_SERVER_ERROR))
                           .build();

    }

    /**
     * Server endpoint for creating a new buy request
     *
     * @return the buy request object if successful, error msg if not
     */
    @POST
    @Path("buy/new")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newBuyRequest(
            BuyRequest newBuyRequest
    ) {
        Optional<BuyRequest> buyRequest = listingService.newBuyRequest(newBuyRequest);

        return buyRequest.map(Response::ok)
                         .orElse(Response.ok("Unexpected error creating the buy request")
                                         .status(Response.Status.INTERNAL_SERVER_ERROR))
                         .build();
    }

    @GET
    @Path("offer/{id}")
    public Response getOfferListing(
            @NotNull @PathParam("id") Long id
    ) {
        Optional<OfferListing> offerListing = listingService.findOfferListingById(id);

        return offerListing.map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @GET
    @Path("buy/{id}")
    public Response getBuyRequest(
            @NotNull @PathParam("id") Long id
    ) {
        Optional<BuyRequest> buyRequest = listingService.findBuyRequestById(id);

        return buyRequest.map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }


    @GET
    @Path("comodity/{id}")
    public Response getCommodityOfferListings(
            @PathParam("id") long id
    ) {
        return Response.ok(listingService.getCommodityOfferListings(id)).build();
    }

    @GET
    @Path("listing/{id}")
    @RolesAllowed(value = {Group.CONTAINER_GROUP_NAME})
    public Response getListingById(
            @PathParam("id") Long id
    ) {
        var listing = listingService.findListingById(id).map(ChatListingInfo::new);
        if (listing.isPresent()) {
            return Response.ok(listing.get()).build();
        } else {
            System.out.println(listing);
            return Response.ok().status(Response.Status.NOT_FOUND).build();
        }
    }
}
