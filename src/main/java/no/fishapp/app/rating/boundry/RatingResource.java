package no.fishapp.app.rating.boundry;


import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.rating.control.RatingService;
import no.fishapp.app.rating.entity.Rating;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("store/rating")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class RatingResource {

    @Inject
    RatingService ratingService;

    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME})
    public Response newRating(
            @NotNull @QueryParam("transactionid") long id,
            @NotNull @QueryParam("stars") int ratingValue
    ) {
        Response.ResponseBuilder resp;

        try {
            Rating rating = ratingService.newRating(id, ratingValue);
            resp = Response.ok(rating.getStars());
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resp.build();

    }


    // this entire method just bad, room for optimization
    @GET
    @Path("{id}")
    public Response getUserRating(
            @PathParam("id") long id
    ) {
        Response.ResponseBuilder resp;

        try {
            double rating = ratingService.getUserRating(id);
            resp = Response.ok(rating);
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return resp.build();
    }
    @GET
    @Path("transaction/{id}")
    @RolesAllowed(value = {Group.USER_GROUP_NAME})
    public Response getTransactionRating(
            @PathParam("id") long id
    ) {
        Response.ResponseBuilder resp;

        try {
            Rating rating = ratingService.getTransactionRating(id);
            int ratingVal = (rating == null)? -1: rating.getStars();
            resp = Response.ok(ratingVal);
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return resp.build();

    }
}
