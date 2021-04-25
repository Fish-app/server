package no.fishapp.auth.control;

import io.smallrye.config.inject.ConfigExtension;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.Group;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WeldJunit5Extension.class)
@Data
@NoArgsConstructor
public class AuthenticationServiceTest {


    @BeforeAll
    static void setEnv() {
        System.setProperty("jwt.cert.file", "target/test_data/jwtkeys.ser");
    }

    @Inject
    AuthenticationService authenticationService;


    // -- bean creators -- //

    public Bean<?> createHasherBean() {
        return MockBean.builder().types(PasswordHash.class).scope(ApplicationScoped.class).creating(
                Mockito.when(Mockito.mock(PasswordHash.class).toString()).thenReturn("aa").getMock()
        ).build();
    }

    public Bean<?> createIdentetyStoreHandlerBean() {
        return MockBean.builder().types(IdentityStoreHandler.class).creating(
                Mockito.when(Mockito.mock(IdentityStoreHandler.class).toString()).thenReturn("a").getMock()
        ).build();
    }

    public Bean<?> createKeyServiceBean() {
        return MockBean.builder().types(KeyService.class).creating(
                Mockito.when(Mockito.mock(KeyService.class).toString()).thenReturn("aaaaaaa").getMock()
        ).build();
    }
    

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
                                                                .addExtensions(ConfigExtension.class)
                                                                .addBeanClasses(AuthenticationService.class)
    )
                                             .addBeans(
                                                     createKeyServiceBean(),
                                                     createIdentetyStoreHandlerBean(),
                                                     createHasherBean()
                                             )
                                             .activate(RequestScoped.class)
                                             .build();


    private Answer<TypedQuery<AuthenticatedUser>> getUserPrincipalAnswer;

    private TypedQuery<AuthenticatedUser> userQueryMock;


    private static Optional<String> jwtSubject;


    final String USER_PRINCIPAL = "test@test.test";
    final long USER_ID = 1337;
    final Set<String> USER_GROUPS = Set.of("TEST1", "TEST2", "TEST3");

    @Test
    void getUserFromPrincipal() {

        // creates the test object
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setPrincipalName(USER_PRINCIPAL);
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setGroups(USER_GROUPS.stream().map(Group::new).collect(Collectors.toList()));


        // verify app params are corectly satt

        TypedQuery<AuthenticatedUser> typedqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(typedqMock.setParameter(Mockito.eq("pname"), Mockito.anyString())).thenReturn(typedqMock);
        Mockito.when(typedqMock.getSingleResult()).thenReturn(Mockito.mock(AuthenticatedUser.class));


        EntityManager mockEntityManager = Mockito.when(
                Mockito.mock(EntityManager.class)
                       .createQuery(AuthenticationService.GET_USER_BY_PRINCIPAL_QUERY,
                                    AuthenticatedUser.class
                       )).thenReturn(typedqMock).getMock();


        weld.select(AuthenticationService.class).get().setEntityManager(mockEntityManager);

        // positive test
        Optional<AuthenticatedUser> testAuthUser = weld.select(AuthenticationService.class)
                                                       .get()
                                                       .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(typedqMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isPresent());

        Mockito.reset(typedqMock);
        Mockito.when(typedqMock.setParameter(Mockito.eq("pname"), Mockito.anyString())).thenReturn(typedqMock);
        Mockito.when(typedqMock.getSingleResult()).thenThrow(NoResultException.class);

        testAuthUser = weld.select(AuthenticationService.class)
                           .get()
                           .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(typedqMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isEmpty());

    }

    @Test
    void getToken() {
    }

    @Test
    void getUserFromId() {
        // creates the test object
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setPrincipalName(USER_PRINCIPAL);
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setGroups(USER_GROUPS.stream().map(Group::new).collect(Collectors.toList()));


        // verify app params are corectly satt

        TypedQuery<AuthenticatedUser> typedqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(typedqMock.setParameter(Mockito.eq("pname"), Mockito.anyString())).thenReturn(typedqMock);
        Mockito.when(typedqMock.getSingleResult()).thenReturn(Mockito.mock(AuthenticatedUser.class));


        EntityManager mockEntityManager = Mockito.when(
                Mockito.mock(EntityManager.class)
                       .createQuery(AuthenticationService.GET_USER_BY_PRINCIPAL_QUERY,
                                    AuthenticatedUser.class
                       )).thenReturn(typedqMock).getMock();


        weld.select(AuthenticationService.class).get().setEntityManager(mockEntityManager);

        // positive test
        Optional<AuthenticatedUser> testAuthUser = weld.select(AuthenticationService.class)
                                                       .get()
                                                       .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(typedqMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isPresent());

        Mockito.reset(typedqMock);
        Mockito.when(typedqMock.setParameter(Mockito.eq("pname"), Mockito.anyString())).thenReturn(typedqMock);
        Mockito.when(typedqMock.getSingleResult()).thenThrow(NoResultException.class);

        testAuthUser = weld.select(AuthenticationService.class)
                           .get()
                           .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(typedqMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isEmpty());

    }

    @Test
    void getCurrentAuthUser() {
    }

    @Test
    void isPrincipalInUse() {
    }

    @Test
    void createUser() {
    }

    @Test
    void changePassword() {
    }
}