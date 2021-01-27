package no.maoyi.app.util;

import no.maoyi.app.resources.entity.Image;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageUtil {


    /**
     * Extracts and saves the the images from a {@link FormDataMultiPart} at the provided field name to the provided save dir and
     * returns a list of {@link Image} objects saved.
     * <p>
     * If no photos are found a empty list is returned.
     *
     * @param photos  The form data part with the images.
     * @param saveDir The dir to save the photos to.
     *
     * @return A list with the photos found.
     * @throws IOException
     */
    public List<Image> saveImages(FormDataMultiPart photos, File saveDir, String fieldName) throws IOException {
        if (photos == null) {
            return null;
        }
        List<FormDataBodyPart> images     = photos.getFields(fieldName);
        List<Image>            saveImages = new ArrayList<>();


        if (images != null) {
            for (FormDataBodyPart part : images) {
                InputStream        inputStream = part.getEntityAs(InputStream.class);
                ContentDisposition meta        = part.getFormDataContentDisposition();

                String saveName = UUID.randomUUID().toString() + FileUtils.getFilePathExtension(meta.getFileName());
                long   size     = Files.copy(inputStream, Paths.get(saveDir.toString(), saveName));

                Image image = new Image();
                image.setName(saveName);
                image.setSize(size);
                image.setMimeType(URLConnection.guessContentTypeFromName(meta.getFileName()));
                saveImages.add(image);
            }
        }
        return saveImages;
    }
}
