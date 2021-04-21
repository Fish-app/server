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


@ApplicationScoped
public class CommodityService {

    private static final String GET_ALL_COMMODITIES = "SELECT c from Commodity c";
    private static final String GET_DISPLAY_COMMODITIES = "SELECT DISTINCT c FROM Commodity c INNER JOIN Listing l ON l.commodity.id = c.id WHERE l.isOpen = true";


    @PersistenceContext
    EntityManager entityManager;


    @Inject
    @RestClient
    ImageClient imageClient;


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


    public List<Commodity> getAllCommodities() {
        return entityManager.createQuery(GET_ALL_COMMODITIES, Commodity.class).getResultList();
    }

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

    public Optional<Commodity> getCommodity(long id) {
        return Optional.ofNullable(entityManager.find(Commodity.class, id));
    }


}
