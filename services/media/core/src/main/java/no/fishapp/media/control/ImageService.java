package no.fishapp.media.control;

import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;


@ApplicationScoped
public class ImageService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "images/items")
    String imageStoragePath;


    public Image saveImage(NewImageDto imageDto) throws MultipartReadException, MultipartNameNotFoundException, IOException {
        Image image = new Image();

        image.setMimeType(imageDto.getMimeType());
        entityManager.persist(image);
        entityManager.flush();


        image.setName(image.getId().toString() + "--" + imageDto.getName());

        int size = (int) Files.copy(imageDto.getImageDataStream(),
                                    java.nio.file.Path.of(imageStoragePath, image.getName()));
        image.setSize(size);
        entityManager.persist(image);
        return image;

    }

    /**
     * Utility method for getting the file ending of the provided file path.
     * If no ending is found null is returned.
     * The dot is included so abc.jpg yields .jpg and abc yields null.
     *
     * @param filePath the filepath to get the extension from.
     * @return the file extension to the file at the provided path inclusive the dot null if no extension.
     */
    public String getFilePathExtension(String filePath) {
        int lastDot = filePath.lastIndexOf(".");
        if (lastDot > 0) {
            return filePath.substring(lastDot);
        } else {
            return null;
        }
    }

}
