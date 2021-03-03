package no.fishapp.app.listing.control;

import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.Listing;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

@Transactional
public class ListingService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserService userService;


    /**
     * Creates a new offer listing
     *
     * @return the created offer listing object
     */
    public OfferListing newOfferListing(OfferListing listing) {
        listing.setCreator(userService.getLoggedInUser());
        entityManager.persist(listing);
        entityManager.refresh(listing);
        return listing;
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
        entityManager.refresh(buyRequest);

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


}
