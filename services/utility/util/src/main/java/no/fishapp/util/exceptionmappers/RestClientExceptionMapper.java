package no.fishapp.util.exceptionmappers;

import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Log
public class RestClientExceptionMapper implements ResponseExceptionMapper<RestClientHttpException> {

    @Override
    public RestClientHttpException toThrowable(Response response) {
        return new RestClientHttpException(response);
    }

    @Override
    public int getPriority() {
        return ResponseExceptionMapper.super.getPriority();
    }

    @Override
    public boolean handles(int status, MultivaluedMap headers) {
        return status >= 300;
    }
}
