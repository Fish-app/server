package no.fishapp.user.client.containerauth;


import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import lombok.extern.java.Log;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
@Log
@Singleton
@Startup
public class RestClientAuthHandler {

    private static RestClientAuthHandler instance;

    public static RestClientAuthHandler getInstance(){
        if (instance == null){
            //todo: this is a issue
            instance = new RestClientAuthHandler();
        }
        return instance;
    }


    @Inject
    @ConfigProperty(name = "fishapp.service.username", defaultValue = "fishapp")
    private String username;

    @Inject
    @ConfigProperty(name = "fishapp.service.password", defaultValue = "fishapp")
    private String password;


    @Inject
    @RestClient
    ContainerAuthClient authClient;

    private JwtParser jwtParser;

    @PostConstruct
    public void init(){
        log.log(Level.WARNING,"created rest auth handler");
        refreshToken();
        instance = this;

    }


    private Jwt jsonWebToken;
    private String tokenString;




    public String getAuthTokenHeader() {
        return tokenString;
    }


    @Schedule(minute = "30", persistent = true)
    private void refreshToken(){
        //todo: if failed wait 30 sec and try again elns

        Response response = authClient.login(new UsernamePasswordData(password, username));





        String authHeader = (String) response.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            System.out.println(authHeader);
            String token = authHeader.replaceFirst("Bearer ", "");
            try {
                this.jsonWebToken =jwtParser.parse(token);
                this.tokenString = authHeader;
                log.log(Level.ALL, "Refreshed jwt token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
