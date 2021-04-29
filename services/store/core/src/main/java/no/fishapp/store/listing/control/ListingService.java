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

/**
 * Manages adding and getting {@link Listing}.
 * Uses {@link EntityManager} to communicate with the database.
 */
@Transactional
public class ListingService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Claim(Claims.SUBJECT)
    Instance<Optional<String>> jwtSubject;

    @Inject
    CommodityService commodityService;

    private static final String COMMODITY_LISTINGS = "select ls from OfferListing ls where ls.commodity.id = :cid";


    private static final String FIND_CHEAPEST = "select min(ls.price) from OfferListing ls where ls.commodity.id = :cid";

    /**
     * Returns the lowest price of a {@link OfferListing} with the provided {@link Commodity} id
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
     * Creates a new {@link OfferListing} with the {@code listing} argument and returns an {@link Optional}
     * containing the resulting {@code OfferListing}.
     * @param listing the {@code OfferListing} to be added
     * @return an {@code Optional} containing the created {@code OfferListing} if successful or {@code empty} if not
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
     * Creates a new {@link BuyRequest} with the {@code buyRequest} argument and returns an {@link Optional} containing
     * the resulting {@code BuyRequest}.
     * @param buyRequest the {@code BuyRequest} to be added
     * @return an {@code Optional} containing the created {@code BuyRequest} if successful or {@code empty} if not
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

    /**
     * Returns a {@link List} containing all {@link OfferListing} connected to the {@link Commodity} matching the id argument.
     * @param id the id of the {@code Commodity} you want the connected {@code OfferListing} of
     * @return a {@code List} containing all of the found {@code OfferListing}
     */
    public List<OfferListing> getCommodityOfferListings(long id) {
        var query = entityManager.createQuery(COMMODITY_LISTINGS, OfferListing.class);
        query.setParameter("cid", id);

        return query.getResultList();
    }

    /**
     * Returns an {@link Optional} containing the {@link OfferListing} with an id matching the id argument.
     * @param listingId the id of the {@code OfferListing} to be found
     * @return an {@code Optional} containing the {@code OfferListing} if found or {@code empty} if not
     */
    public Optional<OfferListing> findOfferListingById(long listingId) {
        return Optional.ofNullable(entityManager.find(OfferListing.class, listingId));

    }

    /**
     * Returns an {@link Optional} containing the {@link BuyRequest} with an id matching the id argument.
     * @param requestId the id of the {@code BuyRequest} to be found
     * @return an {@code Optional} containing the {@code BuyRequest} if found of {@code empty} if not
     */
    public Optional<BuyRequest> findBuyRequestById(long requestId) {
        return Optional.ofNullable(entityManager.find(BuyRequest.class, requestId));
    }

    /**
     * Returns an {@link Optional} containing the {@link Listing} with an id matching the id argument.
     * @param requestId the id of the {@code Listing} to be found
     * @return an {@code Optional} containing the {@code Listing} if found or {@code empty} if not
     */
    public Optional<Listing> findListingById(long requestId) {
        try {
            return Optional.ofNullable(entityManager.find(Listing.class, requestId));
        } catch (PersistenceException pe) {
            return Optional.empty();
        }
    }

}
