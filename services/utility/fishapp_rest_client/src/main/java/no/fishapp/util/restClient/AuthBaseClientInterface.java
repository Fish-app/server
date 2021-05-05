package no.fishapp.util.restClient;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * The token handler for giving the fishapp {@link RestClient}'s a valid token to safely communicate.
 * <p>
 * To use the client simpley extend the {@code AuthBaseClientInterface} and annotate the {@link org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam}
 * with a value fethed from the getAuthToken method.
 *
 * <pre>
 *      @RegisterRestClient()
 *      @ClientHeaderParam(name = &quot;Authorization&quot;, value = &quot;{getAuthToken}&quot;)
 *      public interface ImageClient extends AuthBaseClientInterface {
 *          ...
 *      }
 * </pre>
 * <p>
 * The {@code AuthBaseClientInterface} will then provide up to date token that are validated aginst the public sighing key.
 * The tokens are automaticly refreshed before expiry
 */
public interface AuthBaseClientInterface {

    //TODO: maby make the exep mapper refresh token on 401

    default String getAuthToken() {
        return RestClientAuthHandler.getInstance().getAuthTokenHeader();
    }
}
