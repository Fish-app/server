package no.fishapp.auth.control;

import io.smallrye.config.inject.ConfigExtension;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.Group;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.ExcludeBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.transaction.Transactional;

import java.util.*;
import java.util.function.Function;
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


    //    @WeldSetup
    //    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
    //                                                                .addExtensions(ConfigExtension.class)
    //                                                                .addBeanClasses(AuthenticationService.class
    //                                                                ))
    //                                             .addBeans(
    //                                                     createEntityManagerMock(),
    //                                                     createHasherBean(),
    //                                                     createKeyServiceBean(),
    //                                                     createIdentetyStoreHandlerBean()
    //                                             )
    //                                             .activate(RequestScoped.class)
    //                                             .build();
    //
    //    public Bean<?> createHasherBean() {
    //        System.out.println("\n\n\n\n\nHHHHHHHHHHHHHHHHH");
    //        return MockBean.builder().types(PasswordHash.class).scope(ApplicationScoped.class).creating(
    //                Mockito.when(Mockito.mock(PasswordHash.class).toString()).thenReturn("aa")
    //        ).build();
    //    }
    //
    //    public Bean<?> createIdentetyStoreHandlerBean() {
    //        System.out.println("\n\n\n\n\nSSSSSSSSSSSSSSSSSSSS");
    //
    //        return MockBean.builder().types(IdentityStoreHandler.class).creating(
    //                Mockito.when(Mockito.mock(IdentityStoreHandler.class).toString()).thenReturn("a")
    //        ).build();
    //    }
    //
    //    public Bean<?> createKeyServiceBean() {
    //        System.out.println("\n\n\n\n\nKKKKKKKKKKKKKKKKK");
    //
    //        return MockBean.builder().types(KeyService.class).creating(
    //                Mockito.when(Mockito.mock(KeyService.class).toString()).thenReturn("aa")
    //        ).build();
    //    }
    //
    public Bean<?> createEntityManagerMock() {
        System.out.println("\n\n\n\n\nEEEEEEEEEEEEEEEE");

        return MockBean.builder()
                       .types(EntityManager.class)
                       .scope(ApplicationScoped.class)
                       .creating(
                               Mockito.when(
                                       Mockito.mock(EntityManager.class)
                                              .createQuery(AuthenticationService.GET_USER_BY_PRINCIPAL_QUERY,
                                                           AuthenticatedUser.class
                                              ))
                                      .thenReturn(userQueryMock)
                       ).build();
    }

    //    @Produces
    //    @ExcludeBean   // Excludes beans with type Foo from automatic discovery
    //    PasswordHash hasher = Mockito.mock(PasswordHash.class);

    // @Inject
    //AuthenticationService authenticationService;
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
                                                                .addExtensions(ConfigExtension.class)
                                                                .addBeanClasses(AuthenticationService.class))
                                             .addBeans(
                                                     MockBean.of(Mockito.mock(EntityManager.class), EntityManager.class)
                                                     //createEntityManagerMock()
                                             )
                                             .activate(RequestScoped.class)
                                             .build();
    // @WeldSetup
    //    public WeldInitiator weld = WeldInitiator.from(AuthenticationService.class, AuthenticationServiceTest.class)
    //                                             .activate(RequestScoped.class)
    //                                             .build();


    private Answer<TypedQuery<AuthenticatedUser>> getUserPrincipalAnswer;

    private TypedQuery<AuthenticatedUser> userQueryMock;


    private static Optional<String> jwtSubject;

    //
    //    @ApplicationScoped
    //    @Produces
    //    Instance<Optional<String>> produceJwtSubject() {
    //        return Mockito.when(Mockito.mock(Instance.class).get()).thenReturn(jwtSubject).getMock();
    //    }
    //
    //    @ApplicationScoped
    //    @Produces
    //    PasswordHash produceHasher() {
    //        System.out.println("\n\n\n\n\nAAAAAAAAAAAAAAAAAa");
    //        return Mockito.mock(PasswordHash.class);
    //
    //    }
    //
    @ApplicationScoped
    @Produces
    IdentityStoreHandler produceIdentityStoreHandler() {
        System.out.println("AAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n");
        return Mockito.mock(IdentityStoreHandler.class);

    }

    @ApplicationScoped
    @Produces
    EntityManager produceEntityManager() {
        System.out.println("BBBBBBBBBBB\n\n\n\n\n\n\n\n\n");
        return Mockito.when(
                Mockito.mock(EntityManager.class)
                       .createQuery(AuthenticationService.GET_USER_BY_PRINCIPAL_QUERY,
                                    AuthenticatedUser.class
                       ))
                      .thenReturn(userQueryMock).getMock();

    }


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
        Answer<AuthenticatedUser> authenticatedUserAnswer = null;

        TypedQuery<AuthenticatedUser> typedqMock = Mockito.mock(TypedQuery.class);

        Mockito.when(typedqMock.setParameter(Mockito.eq("pname"), Mockito.anyString())).thenReturn(typedqMock);
        Mockito.when(typedqMock.getSingleResult()).thenReturn(Mockito.mock(AuthenticatedUser.class));

        var a = authenticationService.getUserFromPrincipal(USER_PRINCIPAL);
        Optional<AuthenticatedUser> testAuthUser = weld.select(AuthenticationService.class)
                                                       .get()
                                                       .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(typedqMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));

        assertTrue(testAuthUser.isPresent());
        System.out.println(testAuthUser.get());

        //        userQueryMock = typedqMock;
        //
        //
        //        AuthenticatedUser getSingleResultSuccessMock = Mockito.when(Mockito.mock(TypedQuery.class).getSingleResult())
        //                                                              .thenReturn(authenticatedUser)
        //                                                              .getMock();
        //        AuthenticatedUser getSingleResultExeptMock   = Mockito.when(Mockito.mock(TypedQuery.class).getSingleResult())
        //                                                              .thenThrow(NoResultException.class)
        //                                                              .getMock();
        //        AuthenticatedUser activeMock;
        //
        //        activeMock = getSingleResultSuccessMock;
        //
        //        TypedQuery<AuthenticatedUser> setVarMock = Mockito.when(Mockito.mock(TypedQuery.class)
        //                                                                       .setParameter("pname", Mockito.anyString()))
        //                                                          .getMock();
        //
        //
        //        Function<AuthenticatedUser, AuthenticatedUser> getResultMock = (AuthenticatedUser authUser) -> Mockito.when(
        //                setVarMock.getSingleResult()).thenReturn(authUser).getMock();
        //
        //        this.userQueryMock = getResultMock.apply(getSingleResultSuccessMock)
        //        this.authenticationService.getUserFromPrincipal()
        //
        //        // verify the right param is passed to the query
        //        Mockito.verify(setVarMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));


        //        AuthenticatedUser sucsessQuery = Mockito.when(paramSetQuery.getSingleResult())
        //                                                .thenReturn(authenticatedUser)
        //                                                .getMock();
        //        AuthenticatedUser noResultQuery = Mockito.when(paramSetQuery.getSingleResult())
        //                                                 .thenThrow(NoResultException.class)
        //                                                 .getMock();
        //
        //        authenticatedUserTypedQuery = sucsessQuery;
    }

    @Test
    void getToken() {
    }

    @Test
    void getUserFromId() {
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