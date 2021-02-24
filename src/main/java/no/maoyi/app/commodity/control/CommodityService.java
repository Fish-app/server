package no.maoyi.app.commodity.control;


import no.maoyi.app.commodity.entity.Commodity;
import no.maoyi.app.resources.entity.Image;
import no.maoyi.app.util.ImageUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommodityService {

    private static final String getAllCommodities = "SELECT c from Commodity c";

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken token;

    @Inject
    ImageUtil imageUtil;

    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "photos")
    String photoSaveDir;

    public Commodity addNewCommodity(String name, FormDataMultiPart photo
    ) throws IOException {
        Commodity   commodity = new Commodity();
        List<Image> images    = imageUtil.saveImages(photo, new File(photoSaveDir), "image");


        if (images.isEmpty()) {
            return null;
        } else {
            commodity.setName(name);
            commodity.setCommodityImage(images.get(0));
            entityManager.persist(commodity);
            return commodity;
        }
    }


    public List<Commodity> getAllCommodities() {
        return entityManager.createQuery(getAllCommodities, Commodity.class).getResultList();

    }


}
