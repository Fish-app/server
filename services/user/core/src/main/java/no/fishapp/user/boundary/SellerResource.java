package no.fishapp.user.boundary;


import no.fishapp.auth.model.Group;
import no.fishapp.user.control.SellerService;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import no.fishapp.user.model.user.DTO.SellerNewData;
import no.fishapp.user.model.user.Seller;
import no.fishapp.util.exceptionmappers.RestClientHttpException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;


@Path("seller")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(value = {Group.SELLER_GROUP_NAME})
public class SellerResource {

    @Inject
    SellerService sellerService;

    @POST
    @Path("id-list")
    @RolesAllowed(value = {Group.CONTAINER_GROUP_NAME})
    public Response getAllFromIds(List<Long> userIdList) {
        return Response.ok(sellerService.getSellersFromIdList(userIdList)).build();
    }

    /**
     * Returns the currently logged in seller, if not a seller a error is returned.
     *
     * @return the current seller object.
     */
    @GET
    @Path("current")
    public Response getSeller() {
        Optional<Seller> seller = sellerService.getLoggedInSeller();
        return seller.map(Response::ok)
                     .orElse(Response.ok("Could not find seller").status(Response.Status.INTERNAL_SERVER_ERROR))
                     .build();
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
            SellerNewData sellerNewData) {
        Response.ResponseBuilder resp;
        try {
            var newSeller = sellerService.createSeller(sellerNewData);
            resp = Response.ok(newSeller);

        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user").status(500);
        } catch (UsernameAlreadyInUseException e) {
            resp = Response.ok("User already exist").status(Response.Status.CONFLICT);
        } catch (RestClientHttpException e) {
            //todo: use an jaxrs exeption mapper insted
            resp = Response.serverError();
        }
        return resp.build();
    }
}
