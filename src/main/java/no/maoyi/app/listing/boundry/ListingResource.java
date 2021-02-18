package no.***REMOVED***.app.listing.boundry;


import no.***REMOVED***.app.auth.entity.Group;
import no.***REMOVED***.app.listing.control.ListingService;
import no.***REMOVED***.app.listing.entity.BuyRequest;
import no.***REMOVED***.app.listing.entity.OfferListing;
import no.***REMOVED***.app.user.control.UserService;
import no.***REMOVED***.app.user.entity.User;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.math.BigInteger;

@Path("listing")
public class ListingResource {

    @Inject
    ListingService listingService;

    @Inject
    UserService userService;

    /**
     * Server endpoint for creating a new offer listing
     *
     * @param endDate End date for the offering
     * @param commodityId the id of the commodity being sold
     * @param price The price the commodity is sold at
     * @param maxAmount The maximum total amount of the commodity
     * @param latitude The latitude for the pickup point
     * @param longitude The longitude for the pickup point
     *
     * @return return the offer listing if successful, error msg if not
     */
    @POST
    @Path("newOfferListing")
    @RolesAllowed(value = {Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newOfferListing(
            @NotNull @FormDataParam("endDate") long endDate,
            @NotNull @FormDataParam("commodity") long commodityId,
            @NotNull @FormDataParam("price") double price,
            @NotNull @FormDataParam("maxAmount") int maxAmount,
            @NotNull @FormDataParam("latitude") double latitude,
            @NotNull @FormDataParam("longitude") double longitude
    ) {
        Response.ResponseBuilder resp;
        User user = userService.getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.FORBIDDEN);
        } else {
            try {
                OfferListing offerListing = listingService.newOfferListing(endDate, commodityId, price, maxAmount,
                        latitude, longitude, user);
                resp = Response.ok(offerListing);
            } catch (PersistenceException e) {
                resp = Response.ok("Unexpected error creating the offer listing").status(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return resp.build();
    }

    /**
     * Server endpoint for creating a new buy request
     *
     * @param endDate End date for the offering
     * @param commodityId the id of the commodity being bought
     * @param price The price the commodity is bought at
     * @param amount The amount of the commodity that is wanted
     * @param info Additional info about the request
     * @param maxDistance Maximum distance wanted to travel
     *
     * @return the buy request object if successful, error msg if not
     */
    @POST
    @Path("newBuyRequest")
    @RolesAllowed(value = {Group.USER_GROUP_NAME, Group.SELLER_GROUP_NAME, Group.ADMIN_GROUP_NAME})
    public Response newBuyRequest(
            @NotNull @FormDataParam("endDate") long endDate,
            @NotNull @FormDataParam("commodity") long commodityId,
            @NotNull @FormDataParam("price") double price,
            @NotNull @FormDataParam("amount") int amount,
            @FormDataParam("info") String info,
            @NotNull @FormDataParam("maxDistance") double maxDistance
    ) {
        Response.ResponseBuilder resp;
        User user = userService.getLoggedInUser();
        if (user == null) {
            resp = Response.ok("Could not find user").status(Response.Status.FORBIDDEN);
        } else {
            try {
                BuyRequest buyRequest = listingService.newBuyRequest(endDate, commodityId, price, amount, info,
                        maxDistance, user);
                resp = Response.ok(buyRequest);
            } catch (PersistenceException e) {
                resp = Response.ok("Unexpected error creating the offer listing").status(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return resp.build();
    }
}
