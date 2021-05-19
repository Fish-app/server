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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implementes the REST HTTP API for the Media-component of the Microservice
 * Manages all HTTP request that are about {@link Image}.
 * <p>
 */
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
     * Server endpoint for getting an {@link Image}
     * Returns the {@code Image} with the given id. If the width parameter is set, it will
     * scale the image width/height to the specified width.
     *
     * @param id    id of the image to be found
     * @param width desired return width
     * @return a {@link Response} containing the {@code Image} if found or {@link Response.Status#NOT_FOUND} if not
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.WILDCARD)
    public Response getPhoto(@Positive @PathParam("id") int id, @QueryParam("width") int width) {
        Image imageObject = entityManager.find(Image.class, BigInteger.valueOf(id));
        if (imageObject != null) {
            StreamingOutput result = (OutputStream outputStream) -> {
                java.nio.file.Path image = Paths.get(imageStoragePath, imageObject.getName());
                if (width == 0) {
                    Files.copy(image, outputStream);
                    outputStream.flush();
                } else {
                    Thumbnails.of(image.toFile()).size(width, width).useOriginalFormat().toOutputStream(outputStream);
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

    /**
     * Server endpoint for saving an {@link Image}.
     *
     * @param filename    the filename of the {@code Image}
     * @param mimetype    the mimetype of the {@code Image}
     * @param inputStream the {@link InputStream} containing the {@code Image}
     * @return a {@link Response} containing the saved {@code Image} if successful or
     * {@link Response.Status#INTERNAL_SERVER_ERROR} if not
     */
    @PUT
    @Path("new")
    @RolesAllowed(Group.CONTAINER_GROUP_NAME)
    public Response saveImage(
            @HeaderParam("name") String filename, @HeaderParam("mimetype") String mimetype, InputStream inputStream) {
        Response response;
        try {
            NewImageDto imageDto = new NewImageDto();

            imageDto.setName(filename);
            imageDto.setMimeType(mimetype);
            imageDto.setImageDataStream(inputStream);
            Image image = imageService.saveImage(imageDto);
            response = Response.ok(image).build();
        } catch (MultipartNameNotFoundException | MultipartReadException | IOException e) {
            response = Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }


}
