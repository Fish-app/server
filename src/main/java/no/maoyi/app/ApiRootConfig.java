package no.***REMOVED***.app;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("/api")
public class ApiRootConfig extends ResourceConfig {
    public ApiRootConfig() {
        register(MultiPartFeature.class);
        packages(true,"no.twct.recipeheaven" );
    }

}
