package no.fishapp.store.commodity.control;


import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.store.client.ImageClient;
import no.fishapp.store.model.commodity.Commodity;
import no.fishapp.util.multipartHandler.MultipartHandler;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.activation.DataHandler;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class CommodityService {

    private static final String getAllCommodities = "SELECT c from Commodity c";


    @PersistenceContext
    EntityManager entityManager;



    @Inject
    @RestClient
    ImageClient imageClient;

    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "photos")
    String photoSaveDir;

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



        var imageFuture = imageClient.addImage(handler.getName(),handler.getContentType(), handler.getInputStream());

        //var imageFuture = imageClient.addAuthUser();

        Commodity commodity = new Commodity();
        commodity.setName(commodityName);

        Image image = imageFuture.toCompletableFuture().join();
        //Long image = imageFuture.toCompletableFuture().join();

        if (image == null) {
            throw new MultipartReadException("error saving image", "image");
        } else {
            entityManager.persist(image);
            //commodity.setCommodityImage(image);
            commodity.setImageId(image.getId().longValue());
            //commodity.setImageId(image);

            entityManager.persist(commodity);
            return commodity;
        }

    }


    public List<Commodity> getAllCommodities() {
        return entityManager.createQuery(getAllCommodities, Commodity.class).getResultList();
    }

    public Commodity getCommodity(long id) {
        return entityManager.find(Commodity.class, id);
    }


}
