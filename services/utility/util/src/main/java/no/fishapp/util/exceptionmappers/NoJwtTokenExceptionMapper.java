package no.fishapp.util.exceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoJwtTokenExceptionMapper implements ExceptionMapper<NoJwtTokenException> {

    /**
     * Map an exception to a {@link Response}. Returning
     * {@code null} results in a {@link Response.Status#NO_CONTENT}
     * response. Throwing a runtime exception results in a
     * {@link Response.Status#INTERNAL_SERVER_ERROR} response.
     *
     * @param exception the exception to map to a response.
     * @return a response mapped from the supplied exception.
     */
    @Override
    public Response toResponse(NoJwtTokenException exception) {
        System.out.println("wop wop første frosjøk");
        return Response.ok().status(Response.Status.UNAUTHORIZED).build();
    }
}
