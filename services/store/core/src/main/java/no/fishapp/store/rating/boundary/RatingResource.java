package no.fishapp.store.rating.boundary;


import no.fishapp.auth.model.Group;
import no.fishapp.store.model.rating.Rating;
import no.fishapp.store.model.transaction.Transaction;
import no.fishapp.store.rating.control.RatingService;
import no.fishapp.user.model.user.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Manages all HTTP requests that are about {@link Rating}
 */
@Path("rating")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class RatingResource {

    @Inject
    RatingService ratingService;

    /**
     * Server endpoint for creating a new {@link Rating} on the {@link Transaction} matching the id argument.
     *
     * @param id          the id of the {@code Transaction} the {@code Rating} is connected to
     * @param ratingValue the value of the {@code Rating}
     * @return {@link Response} containing the value of the {@code Rating} if successful or
     * {@link Response.Status#INTERNAL_SERVER_ERROR} if not
     */
    @POST
    @RolesAllowed(value = {Group.USER_GROUP_NAME})
    public Response newRating(
            @NotNull @QueryParam("transactionid") long id, @NotNull @QueryParam("stars") int ratingValue) {
        Optional<Rating> ratingOptional = ratingService.newRating(id, ratingValue);

        return ratingOptional.map(rating -> Response.ok(rating.getStars()))
                             .orElse(Response.ok().status(Response.Status.UNAUTHORIZED)).build();

    }

    /**
     * Server endpoint for getting the average rating of a {@link User}.
     *
     * @param id the id of the {@code User} to find the rating of
     * @return {@link Response} containing the average rating of a {@code User} if found or {@code -1} if not
     */
    @GET
    @Path("{id}")
    public Response getUserRating(
            @PathParam("id") long id) {
        Optional<Float> ratingOptional = ratingService.getUserRating(id);

        return ratingOptional.map(Response::ok).orElse(Response.ok(-1)).build();
    }

    /**
     * Server endpoint for getting the value of a {@link Rating} for a {@link Transaction}
     * with an id matching the id argument.
     *
     * @param id the id of the {@code Transaction} we want the rating of
     * @return {@link Response} containing the value of value of {@code Rating} for the {@code Transaction} if found or
     * {@code -1} if not
     */
    @GET
    @Path("transaction/{id}")
    @RolesAllowed(value = {Group.USER_GROUP_NAME})
    public Response getTransactionRating(
            @PathParam("id") long id) {
        Optional<Rating> ratingOptional = ratingService.getTransactionRating(id);

        return ratingOptional.map(rating -> Response.ok(rating.getStars())).orElse(Response.ok(-1)).build();

    }
}
