package no.fishapp.app.user.boundry;

import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.user.control.BuyerService;
import no.fishapp.app.auth.control.AuthenticationService;
import no.fishapp.app.user.entity.Buyer;
import no.fishapp.app.user.entity.DTO.BuyerNewData;
import no.fishapp.app.user.entity.DTO.UserNewData;
import no.fishapp.app.user.entity.User;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("user/buyer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.BUYER_GROUP_NAME})
public class BuyerResource {


    @Inject
    BuyerService buyerService;

    @Inject
    AuthenticationService authenticationService;

    /**
     * Returns the current logged in buyer
     *
     * @return the current loged in buyer
     */
    @GET
    @Path("current")
    public Response getCurrentBuyer() {
        Response.ResponseBuilder resp;
        Buyer                    buyer = buyerService.getLoggedInBuyer();
        if (buyer == null) {
            resp = Response.ok("Could not find buyer").status(Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            resp = Response.ok(buyer);
        }
        return resp.build();
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
            String  email            = buyerNewData.getUserName().toLowerCase();
            boolean isPrincipalInUse = authenticationService.isPrincipalInUse(email);

            if (! isPrincipalInUse) {
                User newUser = buyerService.createBuyer(buyerNewData.getName(), email, buyerNewData.getPassword());
                resp = Response.ok(newUser);

            } else {
                resp = Response.ok(
                        "User already exist").status(Response.Status.CONFLICT);
            }

        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the user").status(500);
        }
        return resp.build();

    }


}
