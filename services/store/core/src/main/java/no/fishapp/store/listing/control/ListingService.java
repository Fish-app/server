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
    public Optional<OfferListing> newOfferListing(OfferListing listing) {
        Optional<Commodity> com          = commodityService.getCommodity(listing.getCommodity().getId());
        var                 userIdOption = jwtSubject.get();
        if (com.isPresent() && userIdOption.isPresent()) {
            listing.setCommodity(com.get());
            listing.setCreatorId(Long.parseLong(userIdOption.get()));
            entityManager.persist(listing);
            com.get().getListings().add(listing);
            entityManager.persist(com.get());

            return Optional.of(listing);
        } else {
            return Optional.empty();
        }


    }

    /**
     * Creates a new buy request
     *
     * @return the created buy request
     */
    public Optional<BuyRequest> newBuyRequest(
            BuyRequest buyRequest
    ) {
        Optional<Commodity> com          = commodityService.getCommodity(buyRequest.getCommodity().getId());
        var                 userIdOption = jwtSubject.get();
        if (com.isPresent() && userIdOption.isPresent()) {
            buyRequest.setCommodity(com.get());
            buyRequest.setCreatorId(Long.parseLong(userIdOption.get()));
            entityManager.persist(buyRequest);
            com.get().getListings().add(buyRequest);
            entityManager.persist(com.get());
            return Optional.of(buyRequest);
        } else {
            return Optional.empty();
        }

    }


    public List<OfferListing> getCommodityOfferListings(long id) {
        var query = entityManager.createQuery(COMODITY_LISTINGS, OfferListing.class);
        query.setParameter("cid", id);

        return query.getResultList();
    }

    public Optional<OfferListing> findOfferListingById(long listingId) {
        return Optional.ofNullable(entityManager.find(OfferListing.class, listingId));

    }

    public Optional<BuyRequest> findBuyRequestById(long requestId) {
        return Optional.ofNullable(entityManager.find(BuyRequest.class, requestId));
    }

    public Optional<Listing> findListingById(long requestId) {
        try {
            return Optional.ofNullable(entityManager.find(Listing.class, requestId));
        } catch (PersistenceException pe) {
            System.err.print("\n\n\nERR-JPA: Persistence exception:\n\n\n");
            return Optional.empty();
        }
    }

}
