package no.fishapp.util.exceptionmappers;

import lombok.extern.java.Log;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Log
@Provider
public class NoJwtTokenExceptionMapper implements ExceptionMapper<NoJwtTokenException> {

    @Override
    public Response toResponse(NoJwtTokenException exception) {
        return Response.ok().status(Response.Status.UNAUTHORIZED).build();
    }
}
