package no.fishapp.app.testData.boundry;

import no.fishapp.app.auth.control.AuthenticationService;
import no.fishapp.app.auth.entity.Group;
import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.control.ListingService;
import no.fishapp.app.rating.control.RatingService;
import no.fishapp.app.testData.control.TestDataService;
import no.fishapp.app.transaction.control.TransactionService;
import no.fishapp.app.transaction.entity.Transaction;
import no.fishapp.app.user.control.BuyerService;
import no.fishapp.app.user.control.SellerService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed(value = {Group.ADMIN_GROUP_NAME})
public class TestDataResource {

    @Inject
    TestDataService testDataService;


    @GET
    @Path("all")
    @RolesAllowed({Group.USER_GROUP_NAME})
    public Response FillDbWithTestData() {
        Response.ResponseBuilder resp;

        try {
            List<Transaction> transactions = transactionService.getUserTransactions();
            resp = Response.ok("gucci");
        } catch (PersistenceException e) {
            resp = Response.ok("Unexpected error creating the offer listing")
                           .status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resp.build();
    }

}
