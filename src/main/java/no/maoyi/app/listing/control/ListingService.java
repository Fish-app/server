package no.maoyi.app.listing.control;

import no.maoyi.app.commodity.entity.Commodity;
import no.maoyi.app.listing.entity.BuyRequest;
import no.maoyi.app.listing.entity.Listing;
import no.maoyi.app.listing.entity.OfferListing;
import no.maoyi.app.user.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.math.BigInteger;

@Transactional
public class ListingService {

    @PersistenceContext
    EntityManager entityManager;


    /**
     * Creates a new offer listing
     *
     * @param endDate End date for the offering
     * @param commodityId the id of the commodity being sold
     * @param price The price the commodity is sold at
     * @param maxAmount The maximum total amount of the commodity
     * @param latitude The latitude for the pickup point
     * @param longitude The longitude for the pickup point
     * @param user The creator of the offer listing
     *
     * @return the created offer listing object
     */
    public OfferListing newOfferListing(
            long endDate,
            long commodityId,
            double price,
            int maxAmount,
            double latitude,
            double longitude,
            User user
    ) {
        OfferListing newOffer = new OfferListing();
        newOffer.setEndDate(endDate);
        Commodity commodity = entityManager.find(Commodity.class, commodityId);
        newOffer.setCommodity(commodity);
        newOffer.setPrice(price);
        newOffer.setMaxAmount(maxAmount);
        newOffer.setLatitude(latitude);
        newOffer.setLongitude(longitude);
        newOffer.setCreator(user);
        newOffer.setListingType("offer_listing");

        entityManager.persist(newOffer);

        return newOffer;
    }

    /**
     * Creates a new buy request
     * @param endDate The end date for the request
     * @param commodityId The id of the commodity being bought
     * @param price The price the commodity is bought at
     * @param amount The amount of the commodity that is wanted
     * @param info Additional info about the request
     * @param maxDistance Maximum distance wanted to travel
     * @param user The user creating the request
     *
     * @return the created buy request
     */
    public BuyRequest newBuyRequest(
            long endDate,
            long commodityId,
            double price,
            int amount,
            String info,
            double maxDistance,
            User user
    ) {
        BuyRequest newBuy = new BuyRequest();
        newBuy.setEndDate(endDate);
        Commodity commodity = entityManager.find(Commodity.class, commodityId);
        newBuy.setCommodity(commodity);
        newBuy.setPrice(price);
        newBuy.setAmount(amount);
        if (info == null) {
            info = "";
        }
        newBuy.setInfo(info);
        newBuy.setMaxDistance(maxDistance);
        newBuy.setCreator(user);
        newBuy.setListingType("order_listing");

        entityManager.persist(newBuy);

        return newBuy;
    }


    /**
     *  Returns a listing if found in the persistence backend
     * @param listingId The ID of the listing
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




}
