package no.fishapp.util.restClient.auth;


public interface AuthBaseClientInterface {
    default String getAuthToken(){
        return RestClientAuthHandler.getInstance().getAuthTokenHeader();
    }
}
