package no.fishapp.app.testData.control;

import no.fishapp.app.auth.control.AuthenticationService;
import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.control.ListingService;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.rating.control.RatingService;
import no.fishapp.app.transaction.control.TransactionService;
import no.fishapp.app.user.control.BuyerService;
import no.fishapp.app.user.control.SellerService;
import no.fishapp.app.user.entity.User;

import javax.inject.Inject;
import java.util.List;

public class TestDataService {

    @Inject
    AuthenticationService authenticationService;

    @Inject
    CommodityService commodityService;

    @Inject
    ListingService listingService;

    @Inject
    RatingService ratingService;

    @Inject
    TransactionService transactionService;

    @Inject
    BuyerService buyerService;

    @Inject
    SellerService sellerService;


    public List<User> makeUsers() {
        return null;
    }

    public List<Commodity> makeCommoditys() {
        return null;
    }

    public List<Listing> makeRandomBuyRequestAndOfferListings(List<User> users) {
        return null;
    }

    public List<Listing> makeRandomBuyRequestAndOfferListingsRatings(List<Listing> listings) {
        return null;
    }

    public List<Listing> makeRandomBuyRequestAndOfferListingsChatsWithContent(List<Listing> users) {
        return null;
    }


}
