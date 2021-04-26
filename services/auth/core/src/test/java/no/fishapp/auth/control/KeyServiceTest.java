package no.fishapp.auth.control;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.smallrye.config.inject.ConfigExtension;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Set;

@EnableWeld
class KeyServiceTest {

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    private String issuer;

    @Inject
    KeyService keyService;

    @BeforeAll
    static void setEnv() {
        System.setProperty("jwt.cert.file", "target/test_data/jwtkeys.ser");
    }

    @WeldSetup
    public WeldInitiator weld = WeldInitiator
            .from(WeldInitiator.createWeld().addExtensions(ConfigExtension.class).addBeanClass(KeyService.class))
            .activate(RequestScoped.class).build();

    @Test
    @Order(0)
    void getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        var pkey = weld.select(KeyService.class).get().getPublicKey();
        String publicKeyPEM = pkey.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "")
                                  .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes());

        // test if the key is ok
        KeyFactory         keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec    = new X509EncodedKeySpec(encoded);
        keyFactory.generatePublic(keySpec);
    }

    final String USER_PRINCIPAL = "test@test.test";
    final long USER_ID = 1337;
    final Set<String> USER_GROUPS = Set.of("TEST1", "TEST2", "TEST3");

    @Test
    @Order(1)
    void generateNewJwtToken() throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException {
        var pkey = weld.select(KeyService.class).get().getPublicKey();
        String publicKeyPEM = pkey.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "")
                                  .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes());

        // test if the key is ok
        KeyFactory         keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec    = new X509EncodedKeySpec(encoded);
        PublicKey          publicKey  = keyFactory.generatePublic(keySpec);

        var    jwtParser      = Jwts.parserBuilder().setSigningKey(publicKey).requireIssuer(issuer).build();
        String jwtTokenString = keyService.generateNewJwtToken(USER_PRINCIPAL, USER_ID, USER_GROUPS);

        var jwtClaims = jwtParser.parseClaimsJws(jwtTokenString);

        String            tokenIssuer        = jwtClaims.getBody().getIssuer();
        String            tokenSubject       = jwtClaims.getBody().getSubject();
        String            tokenUserPrincipal = jwtClaims.getBody().get("upn", String.class);
        ArrayList<String> tokenGroups        = jwtClaims.getBody().get("groups", ArrayList.class);

        Assertions.assertEquals(tokenIssuer, issuer);
        Assertions.assertEquals(tokenSubject, String.valueOf(USER_ID));
        Assertions.assertEquals(tokenUserPrincipal, USER_PRINCIPAL);

        Assertions.assertTrue(USER_GROUPS.containsAll(tokenGroups));
        Assertions.assertTrue(tokenGroups.containsAll(USER_GROUPS));
        Assertions.assertEquals(USER_GROUPS.size(), tokenGroups.size());// technically unececery

    }


}