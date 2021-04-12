package no.fishapp.media.boundary;

import net.coobird.thumbnailator.Thumbnails;
import no.fishapp.auth.model.Group;
import no.fishapp.media.control.ImageService;
import no.fishapp.media.model.DTO.NewImageDto;
import no.fishapp.media.model.Image;
import no.fishapp.util.multipartHandler.MultipartNameNotFoundException;
import no.fishapp.util.multipartHandler.MultipartReadException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("image")
@Transactional

public class ImageResource {

    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "images/items")
    String imageStoragePath;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ImageService imageService;

    /**
     * Returns the image with the given id. If the width parameter is set, it will
     * scale the image width/height to the specified witdth.
     *
     * @param id    id of the image
     * @param width desired return width
     * @return image or 404
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.WILDCARD)
    @RolesAllowed(Group.BUYER_GROUP_NAME)
    public Response getPhoto(@Positive @PathParam("id") int id, @QueryParam("width") int width) {
        Image imageObject = entityManager.find(Image.class, BigInteger.valueOf(id));
        if (imageObject != null) {
            StreamingOutput result = (OutputStream outputStream) -> {
                java.nio.file.Path image = Paths.get(imageStoragePath, imageObject.getName());
                if (width == 0) {
                    Files.copy(image, outputStream);
                    outputStream.flush();
                } else {
                    Thumbnails.of(image.toFile())
                              .size(width, width)
                              .outputFormat(imageObject.getMimeType())
                              .toOutputStream(outputStream);
                }
            };

            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);
            return Response.ok(result).cacheControl(cc).type(imageObject.getMimeType()).build();
        } else {
            return Response.ok("Could not find image").status(Response.Status.NOT_FOUND).build();
        }
    }


    @PUT
    @Path("new")
    @RolesAllowed(Group.CONTAINER_GROUP_NAME)
    public Response saveImage(@HeaderParam("name") String filename, @HeaderParam("mimetype")String mimetype, InputStream inputStream) {
        try {
            NewImageDto imageDto = new NewImageDto();

            imageDto.setName(filename);
            imageDto.setMimeType(mimetype);
            imageDto.setImageDataStream(inputStream);
            Image image = imageService.saveImage(imageDto);
            System.out.println(image.getId());
            System.out.println("\n\n\n\n\n");
        } catch (MultipartNameNotFoundException | MultipartReadException | IOException e) {
            e.printStackTrace();
        }

        return Response.ok("test").build();
    }





}
