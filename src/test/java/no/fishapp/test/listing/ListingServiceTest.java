package no.fishapp.test.listing;

import manifold.ext.rt.api.Jailbreak;
import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.listing.control.ListingService;
import no.fishapp.app.listing.entity.BuyRequest;
import no.fishapp.app.listing.entity.OfferListing;
import no.fishapp.app.user.control.UserService;
import no.fishapp.app.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class ListingServiceTest {

    @Mock
    EntityManager em;

    @Mock
    UserService us;

    @Mock
    CommodityService cs;

    @InjectMocks
    ListingService ls;

    private OfferListing listing;
    private BuyRequest buyRequest;
    private Commodity commodity;
    private User user;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
        commodity.setId(1L);
        commodity.setOfferListings(new ArrayList<>());
        user = new User();
    }


    /**
     * Test that the new OfferListing gets the correct commodity and creator set
     * and that it is persisted.
     */
    @Test
    void newOfferListingTest() {
        listing = new OfferListing();
        listing.setCommodity(commodity);
        when(cs.getCommodity(listing.getCommodity().getId())).thenReturn(commodity);
        when(us.getLoggedInUser()).thenReturn(user);
        OfferListing result = ls.newOfferListing(listing);
        assertEquals(result, listing);
        assertEquals(result.getCommodity(), listing.getCommodity());
        assertEquals(result.getCreator(), listing.getCreator());
        verify(em).persist(any());
    }

    /**
     * Test that the new BuyRequest gets the correct commodity and creator set
     * and that it is persisted.
     */
    @Test
    void newBuyRequestTest() {
        buyRequest = new BuyRequest();
        buyRequest.setCommodity(commodity);
        when(cs.getCommodity(buyRequest.getCommodity().getId())).thenReturn(commodity);
        when(us.getLoggedInUser()).thenReturn(user);
        BuyRequest result = ls.newBuyRequest(buyRequest);
        assertEquals(result, buyRequest);
        assertEquals(result.getCommodity(), buyRequest.getCommodity());
        assertEquals(result.getCreator(), buyRequest.getCreator());
        verify(em).persist(any());
    }
}
