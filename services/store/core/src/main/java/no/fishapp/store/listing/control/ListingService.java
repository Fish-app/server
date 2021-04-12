package no.fishapp.store.listing.control;


import io.jsonwebtoken.Claims;
import no.fishapp.store.commodity.control.CommodityService;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.listing.BuyRequest;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.store.model.listing.OfferListing;
import org.eclipse.microprofile.jwt.Claim;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class ListingService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    CommodityService commodityService;

    //private static final String COMODITY_LISTINGS = "select ls from OfferListing ls";
    private static final String COMODITY_LISTINGS = "select ls from OfferListing ls where ls.commodity.id = :cid";


    private static final String FIND_CHEAPEST = "select min(ls.price) from OfferListing ls where ls.commodity.id = :cid";

    /**
     * returns the listing for the provided commodity id with the lowest price
     *
     * @param id the id of the commodity
     * @return the cheapest price or -1 if not found
     */
    public long getCheapestPriceListingFromCommodity(long id) {
        var query = entityManager.createQuery(FIND_CHEAPEST);
        query.setParameter("cid", id);

        try {
            return (long) query.getSingleResult();
        } catch (NoResultException ignore) {
            return -1L;
        }
    }

    /**
     * Creates a new offer listing
     *
     * @return the created offer listing object
     */
    public OfferListing newOfferListing(OfferListing listing) {


        Commodity com = commodityService.getCommodity(listing.getCommodity().getId());
        var userIdOption = jwtSubject.get();
        if (com != null && userIdOption.isPresent()) {
            // todo tror det her kan jøres automatisk
            listing.setCommodity(com);
            listing.setCreatorId(Long.parseLong(userIdOption.get()));
            entityManager.persist(listing);
            com.getListings().add(listing);
            entityManager.persist(com);


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

        var userIdOption = jwtSubject.get();
        if (com != null && userIdOption.isPresent()) {
            buyRequest.setCommodity(com);
            buyRequest.setCreatorId(Long.parseLong(userIdOption.get()));
            entityManager.persist(buyRequest);
            com.getListings().add(buyRequest);
            entityManager.persist(com);
            return buyRequest;
        } else {
            return null;
        }

    }


    /**
     * Returns a listing if found in the persistence backend
     *
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
