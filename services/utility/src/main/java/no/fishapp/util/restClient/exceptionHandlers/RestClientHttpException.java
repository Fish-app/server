package no.fishapp.util.restClient.exceptionHandlers;

import javax.ws.rs.core.Response;

public class RestClientHttpException extends Exception {

    int httpStatusCode;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public RestClientHttpException(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;

    }
}
