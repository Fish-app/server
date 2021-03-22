package no.fishapp.store.core.listing.control;

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


    private static final String FIND_CHEAPEST = "select min(ls.price) from OfferListing ls where ls.commodity.id = :cid";

    /**
     * returns the listing for the provided commodity id with the lowest price
     *
     * @param id the id of the commodity
     *
     * @return the cheapest price or -1 if not found
     */
    public long getCheapestPriceListingFromCommodity(long id) {
        var query = entityManager.createQuery(FIND_CHEAPEST);
        query.setParameter("cid", id);

        try {
            return (long) query.getSingleResult();
        } catch (NoResultException ignore) {
            return - 1L;
        }
    }

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
            listing.setCreator(userService.getLoggedInUser());
            com.getOfferListings().add(listing);
            entityManager.persist(listing);
            //entityManager.persist(com);


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
        Commodity com = commodityService.getCommodity(buyRequest.getCommodity().getId());

        if (com != null) {
            buyRequest.setCommodity(com);
            entityManager.persist(buyRequest);
            buyRequest.setCreator(userService.getLoggedInUser());
            return buyRequest;
        } else {
            return null;
        }

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

    public BuyRequest findBuyRequestById(long requestId) {
        try {
            return entityManager.find(BuyRequest.class, requestId);
        } catch (PersistenceException pe) {
            System.err.print("\n\n\nERR-JPA: Persistence exception:\n\n\n");
            return null;
        }
    }

}
