package no.fishapp.store.client;


import no.fishapp.user.model.user.Buyer;
import no.fishapp.user.model.user.Seller;
import no.fishapp.util.exceptionmappers.RestClientHttpException;
import no.fishapp.util.restClient.AuthBaseClientInterface;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;


@RegisterRestClient(configKey = "userClient")
@Path("/api/user/")
@ClientHeaderParam(name = "Authorization", value = "{getAuthToken}")
public interface UserClient extends AutoCloseable, AuthBaseClientInterface {

    @POST
    @Path("buyer/id-list")
    List<Buyer> getBuyersFromIdList(Set<Long> idSet) throws RestClientHttpException;

    @POST
    @Path("seller/id-list")
    List<Seller> getSellersFromIdList(Set<Long> idSet) throws RestClientHttpException;
}
