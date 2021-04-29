package no.fishapp.store.commodity.control;


import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.store.client.ImageClient;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.store.model.commodity.DTO.CommodityDTO;
import no.fishapp.store.model.listing.Listing;
import no.fishapp.store.model.listing.OfferListing;
import no.fishapp.util.multipartHandler.MultipartHandler;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.activation.DataHandler;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages adding and getting {@link Commodity}.
 * Uses {@link EntityManager} to communicate with the database.
 */
@ApplicationScoped
public class CommodityService {

    /**
     * Returns all the commodities.
     */
    private static final String GET_ALL_COMMODITIES = "SELECT c from Commodity c";

    /**
     * Returns a single instance of each of the commodities that has an active listing.
     */
    private static final String GET_DISPLAY_COMMODITIES = "SELECT DISTINCT c FROM Commodity c INNER JOIN Listing l ON l.commodity.id = c.id WHERE l.isOpen = true";


    @PersistenceContext
    EntityManager entityManager;


    @Inject
    @RestClient
    ImageClient imageClient;


    /**
     * Creates a new {@link Commodity} with data from the provided {@link IMultipartBody}.
     * @param multipartBody the {@code IMultipartBody} containing the data for the new {@code Commodity}
     * @return the created {@code Commodity}
     * @throws IOException if an I/O error occurs with the {@link java.io.InputStream}
     * @throws MultipartReadException if an error occurs with the {@link MultipartHandler} while reading a string
     * @throws MultipartNameNotFoundException if {@link MultipartHandler} can't find a field with specified name
     */
    public Commodity addNewCommodity(
            IMultipartBody multipartBody
    ) throws IOException, MultipartReadException, MultipartNameNotFoundException {
        MultipartHandler multipartHandler = new MultipartHandler(multipartBody);

        String      commodityName = multipartHandler.getFieldAsString("name");
        DataHandler handler       = multipartHandler.getFieldDataHandler("image");

        NewImageDto imageDto = new NewImageDto();
        imageDto.setName(handler.getName());
        imageDto.setMimeType(handler.getContentType());
        imageDto.setImageDataStream(handler.getInputStream());

        var imageFuture = imageClient.addImage(handler.getName(), handler.getContentType(), handler.getInputStream());

        Commodity commodity = new Commodity();
        commodity.setName(commodityName);

        Image image = imageFuture.toCompletableFuture().join();

        if (image == null) {
            throw new MultipartReadException("error saving image", "image");
        } else {
            entityManager.persist(image);
            //commodity.setCommodityImage(image);
            commodity.setCommodityImage(image);
            //commodity.setImageId(image);

            entityManager.persist(commodity);
            return commodity;
        }

    }

    /**
     * Returns a {@link List} containing all {@link Commodity}.
     * @return a {@code List} containing all the {@code Commodities}
     */
    public List<Commodity> getAllCommodities() {
        return entityManager.createQuery(GET_ALL_COMMODITIES, Commodity.class).getResultList();
    }

    /**
     * Gets a {@link List} with a single instance of each {@link Commodity} with an active listing.
     * List of Commodities gets turned into list of {@link CommodityDTO} and is then returned.
     * @return a {@code List} containing a single instance of each {@code CommodityDTO} with an active listing
     */
    public List<CommodityDTO> getAllDisplayCommodities() {

        List<Commodity> commodities = entityManager.createQuery(GET_DISPLAY_COMMODITIES, Commodity.class).getResultList();


        return commodities.stream()
                          .parallel()
                          .map(commodity -> new CommodityDTO(commodity,
                                                             commodity.getListings()
                                                                      .stream()
                                                                      .filter(listing -> listing instanceof OfferListing)
                                                                      .map(Listing::getPrice)
                                                                      .min((o1, o2) -> (int) (o1 - o2))
                                                                      .orElse(- 1D)
                          ))
                          .collect(Collectors.toList());
    }

    /**
     * Returns an {@link Optional} containing the {@link Commodity} with an id matching the id argument.
     * If no {@code Commodity} with the provided id is found an empty {@code Optional} is returned.
     * @param id the id of the commodity to find
     * @return an {@code Optional} containing the {@code Commodity} with the provided id
     */
    public Optional<Commodity> getCommodity(long id) {
        return Optional.ofNullable(entityManager.find(Commodity.class, id));
    }

}
