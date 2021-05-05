package no.fishapp.util.exceptionmappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InvalidInputDataExceptionMapper implements ExceptionMapper<InvalidInputDataException> {
    
    @Override
    public Response toResponse(InvalidInputDataException exception) {
        return Response.ok(exception.getReturnMessage()).status(Response.Status.BAD_REQUEST).build();
    }
}
