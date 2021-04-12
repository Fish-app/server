package no.fishapp.store.rating.boundary;


import no.fishapp.auth.model.Group;
import no.fishapp.store.model.rating.Rating;
import no.fishapp.store.rating.control.RatingService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("rating")
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
        Optional<Rating> ratingOptional = ratingService.newRating(id, ratingValue);

        return ratingOptional.map(rating -> Response.ok(rating.getStars()))
                             .orElse(Response.ok()
                                             .status(Response.Status.INTERNAL_SERVER_ERROR))
                             .build();


    }


    @GET
    @Path("{id}")
    public Response getUserRating(
            @PathParam("id") long id
    ) {
        Optional<Float> ratingOptional = ratingService.getUserRating(id);

        return ratingOptional.map(Response::ok)
                             .orElse(Response.ok(-1))
                             .build();
    }

    @GET
    @Path("transaction/{id}")
    @RolesAllowed(value = {Group.USER_GROUP_NAME})
    public Response getTransactionRating(
            @PathParam("id") long id
    ) {
        Optional<Rating> ratingOptional = ratingService.getTransactionRating(id);

        return ratingOptional.map(rating -> Response.ok(rating.getStars()))
                             .orElse(Response.ok(-1))
                             .build();

    }
}
