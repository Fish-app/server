package no.fishapp.media.control;

import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.util.multipartHandler.MultipartHandler;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Manages saving and getting {@link Image}
 * Saves images to a filepath. Uses {@link EntityManager} to communicate with the database
 */
@ApplicationScoped
public class ImageService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "images/items")
    String imageStoragePath;

    /**
     * Creates and saves an {@link Image} from a {@link NewImageDto} and returns it.
     * @param imageDto the {@code NewImageDto} containing the image data
     * @return the {@code Image} that was created and saved
     * @throws MultipartReadException if an error occurs with the {@link MultipartHandler} while reading a string
     * @throws MultipartNameNotFoundException if {@link MultipartHandler} can't find a field with specified name
     * @throws IOException if an I/O error occurs
     */
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
