package no.fishapp.app.listing.control;

import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ListingService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;

    @Inject
    CommodityService commodityService;

    //private static final String COMODITY_LISTINGS = "select ls from OfferListing ls";
    private static final String COMODITY_LISTINGS = "select ls from OfferListing ls where ls.commodity.id = :cid";


    /**
     * Creates a new offer listing
     *
     * @return the created offer listing object
     */
    public OfferListing newOfferListing(OfferListing listing) {


        Commodity com = commodityService.getCommodity(listing.getCommodity().getId());
        if (com != null) {
            // todo tror det her kan jøres automatisk
            listing.setCommodity(com);
            entityManager.persist(listing);
            listing.setCreator(userService.getLoggedInUser());

            return listing;
        } else {
            return null;
        }


    }

    /**
     * Creates a new buy request
     *
     * @return the created buy request
     */
    public BuyRequest newBuyRequest(
            BuyRequest buyRequest
    ) {

        buyRequest.setCreator(userService.getLoggedInUser());

        entityManager.persist(buyRequest);

        return buyRequest;
    }


    /**
     * Returns a listing if found in the persistence backend
     *
     * @param listingId The ID of the listing
     *
     * @return null if not found or on failure, otherwise the found listing object
     */
    public Listing findListingById(long listingId) {
        try {
            return entityManager.find(Listing.class, listingId);
        } catch (PersistenceException pe) {
            System.err.print("\n\n\nERR-JPA: Persistence exception:\n\n\n");
            return null;
        }
    }

    public List<OfferListing> getCommodityOfferListings(long id) {
        var query = entityManager.createQuery(COMODITY_LISTINGS, OfferListing.class);
        query.setParameter("cid", id);

        try {
            var numWith = query.getResultList();

            return numWith;
        } catch (NoResultException ignore) {
        }
        return null;
    }

    public OfferListing findOfferListingById(long listingId) {
        try {
            return entityManager.find(OfferListing.class, listingId);
        } catch (PersistenceException pe) {
            System.err.print("\n\n\nERR-JPA: Persistence exception:\n\n\n");
            return null;
        }
    }

}
