package no.fishapp.util.exceptionmappers;

import lombok.Data;

import javax.ws.rs.core.Response;

@Data
public class RestClientHttpException extends Exception {

    Response response;


    public RestClientHttpException(Response response) {
        this.response = response;

    }
}
