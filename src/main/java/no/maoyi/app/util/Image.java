package no.***REMOVED***.app.util;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class Image {

    // TODO: shitty old implementation, improve

    /**
     * tries to return the first image in the form null is returned if unsucsessfull
     *
     * @param photos
     * @return
     * @throws IOException
     */
    private no.***REMOVED***.app.resources.entity.Image savePhoto(FormDataMultiPart photos, File saveDir) throws IOException {
        if (photos == null){return null;}
        List<FormDataBodyPart> images = photos.getFields("image");
        if (images != null) {
            for (FormDataBodyPart part : images) {
                InputStream        inputStream = part.getEntityAs(InputStream.class);
                ContentDisposition meta        = part.getFormDataContentDisposition();

                String saveName = UUID.randomUUID().toString() + FileUtils.getFilePathExtension(meta.getFileName());
                long   size     = Files.copy(inputStream, Paths.get(saveDir.toString(), saveName));

                no.***REMOVED***.app.resources.entity.Image photo = new no.***REMOVED***.app.resources.entity.Image();
                photo.setName(saveName);
                photo.setSize(size);
                photo.setMimeType(URLConnection.guessContentTypeFromName(meta.getFileName()));
                //formPhotos.add(photo);
                return photo;
            }
        }
        return null;
    }
}
