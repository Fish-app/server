package no.fishapp.store.listing.boundary;


import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.store.Exception.UserNotSubscribedException;
import no.fishapp.store.listing.control.ListingService;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.listing.BuyRequest;
import no.fishapp.store.model.listing.DTO.ChatListingInfo;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.store.model.listing.OfferListing;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Handles all HTTP request that are about {@link Listing}
 */
@Path("listing")
@Log
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ListingResource {

    @Inject
    ListingService listingService;

    /**
     * Server endpoint for creating a new {@link OfferListing}.
     *
     * @param newOfferListing the {@code OfferListing} to be added
     * @return {@link Response} containing the new {@code OfferListing}
     */
    @POST
    @Path("offer/new")
    @RolesAllowed(value = {Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newOfferListing(
            @Valid OfferListing newOfferListing) {
        Response response;
        try {
            Optional<OfferListing> offerListing = listingService.newOfferListing(newOfferListing);
            response = offerListing.map(Response::ok).orElse(Response.ok("Invalid_offer_listing_provided")
                                                                     .status(Response.Status.BAD_REQUEST)).build();
        } catch (ExecutionException | InterruptedException e) {
            response = Response.serverError().build();
        } catch (UserNotSubscribedException e) {
            response = Response.ok("Seller is not subscribed").status(Response.Status.UNAUTHORIZED).build();
        }

        return response;

    }

    /**
     * Server endpoint for creating a new {@link BuyRequest}.
     *
     * @param newBuyRequest the {@code BuyRequest} to be added
     * @return {@link Response} containing the new {@code BuyRequest}
     */
    @POST
    @Path("buy/new")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newBuyRequest(
            @Valid BuyRequest newBuyRequest) {
        Optional<BuyRequest> buyRequest = listingService.newBuyRequest(newBuyRequest);

        return buyRequest.map(Response::ok)
                         .orElse(Response.ok("Invalid buy request provided").status(Response.Status.BAD_REQUEST))
                         .build();
    }

    /**
     * Server endpoint for getting an {@link OfferListing} with an id matching the id argument.
     *
     * @param id the id of the {@code OfferListing} to be found
     * @return {@link Response} containing the {@code OfferListing} if one is found or {@link Response.Status#NOT_FOUND} if not
     */
    @GET
    @Path("offer/{id}")
    public Response getOfferListing(
            @NotNull @PathParam("id") Long id) {
        Optional<OfferListing> offerListing = listingService.findOfferListingById(id);

        return offerListing.map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    /**
     * Server endpoint for getting a {@link BuyRequest} with an id matching the id argument.
     *
     * @param id the id of the {@code BuyRequest} to be found
     * @return {@link Response} containing the {@code BuyRequest} if one is found or {@link Response.Status#NOT_FOUND} if not
     */
    @GET
    @Path("buy/{id}")
    public Response getBuyRequest(
            @NotNull @PathParam("id") Long id) {
        Optional<BuyRequest> buyRequest = listingService.findBuyRequestById(id);

        return buyRequest.map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    /**
     * Serve endpoint for getting all {@link OfferListing} connected to the {@link Commodity} matching the id argument.
     *
     * @param id the id of the {@code Commodity} you want all the connected {@code OfferListing} of
     * @return {@link Response} containing a {@link List} with all the found {@code OfferListing}
     */
    @GET
    @Path("commodity/{id}")
    public Response getCommodityOfferListings(
            @PathParam("id") long id) {
        List<OfferListing> a = new ArrayList<>();
        try {
            a = listingService.getCommodityOfferListings(id);
        } catch (Throwable l) {
            System.out.println(l.getMessage());
            l.printStackTrace();
        }
        return Response.ok(a).build();
    }

    /**
     * Inter container communication endpoint used to chek if a listing exists and are open and active.
     * Returns the {@link ChatListingInfo} for provided {@link Listing} id
     *
     * @param id the id of the {@code Listing} to be found
     * @return {@link Response} containing the {@link ChatListingInfo} if the {@code Listing} found or {@link Response.Status#NOT_FOUND} if not
     */
    @GET
    @Path("listing/cli/{id}")
    @RolesAllowed(value = {Group.CONTAINER_GROUP_NAME})
    public Response getChatListingInfo(
            @PathParam("id") long id) {
        return listingService.findListingById(id).map(ChatListingInfo::new).map(Response::ok)
                             .orElse(Response.ok().status(Response.Status.NOT_FOUND)).build();

    }
}


