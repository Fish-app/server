package no.fishapp.util.restClient;


public interface AuthBaseClientInterface {

    //TODO: maby make the exep mapper refresh token on 401

    default String getAuthToken() {
        return RestClientAuthHandler.getInstance().getAuthTokenHeader();
    }
}
