package no.fishapp.user.client.containerauth;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import javax.inject.Inject;


public interface AuthBaseClientInterface {
    default String getAuthToken(){
        return RestClientAuthHandler.getInstance().getAuthTokenHeader();
    }
}
