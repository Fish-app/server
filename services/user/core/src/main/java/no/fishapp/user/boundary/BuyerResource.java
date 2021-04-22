package no.fishapp.user.boundary;


import lombok.extern.java.Log;
import no.fishapp.auth.model.Group;
import no.fishapp.user.control.BuyerService;
import no.fishapp.user.exception.UsernameAlreadyInUseException;
import no.fishapp.user.model.user.Buyer;
import no.fishapp.user.model.user.DTO.BuyerNewData;
import no.fishapp.util.restClient.exceptionHandlers.RestClientHttpException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("buyer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.BUYER_GROUP_NAME})
@Log
public class BuyerResource {


    @Inject
    BuyerService buyerService;


    /**
     * Returns the current logged in buyer
     *
     * @return the current loged in buyer
     */
    @GET
    @Path("current")
    public Response getCurrentBuyer() {
        Optional<Buyer> buyer = buyerService.getLoggedInBuyer();
        return buyer.map(Response::ok)
                    .orElse(Response.ok("Could not find buyer").status(Response.Status.INTERNAL_SERVER_ERROR))
                    .build();
    }

    /**
     * Creates a new buyer in the system.
     *
     * @return returns the buyer if successful error msg if not
     */
    @POST
    @Path("create")
    @PermitAll
    public Response createBuyer(
            BuyerNewData buyerNewData
    ) {
        Response.ResponseBuilder resp;
        try {
            var newBuyer = buyerService.createBuyer(buyerNewData);
            resp = Response.ok(newBuyer);

        } catch (UsernameAlreadyInUseException e) {
            resp = Response.ok(
                    "User already exist").status(Response.Status.CONFLICT);
        } catch (RestClientHttpException e) {
            //todo: use an jaxrs exeption mapper insted
            resp = Response.serverError();
        }
        return resp.build();

    }


}
