package no.fishapp.user.client;

import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Log
public class RestClientExceptionMapper implements ResponseExceptionMapper<RestClientErrorException> {
    @Override
    public RestClientErrorException toThrowable(Response response) {
        return new RestClientErrorException();
    }

    @Override
    public int getPriority() {
        return ResponseExceptionMapper.super.getPriority();
    }

    @Override
    public boolean handles(int status, MultivaluedMap headers) {
        log.info("status = " + status);
        return status == 404;
    }
}
