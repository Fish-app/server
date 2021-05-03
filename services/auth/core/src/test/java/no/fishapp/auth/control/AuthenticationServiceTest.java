package no.fishapp.auth.control;

import io.smallrye.config.inject.ConfigExtension;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.auth.model.AuthenticatedUser;
import no.fishapp.auth.model.DTO.NewAuthUserData;
import no.fishapp.auth.model.DTO.UsernamePasswordData;
import no.fishapp.auth.model.Group;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WeldJunit5Extension.class)
@Data
@NoArgsConstructor
public class AuthenticationServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld().addExtensions(ConfigExtension.class)
                                                                .addBeanClasses(AuthenticationServiceTest.class,
                                                                                AuthenticationService.class
                                                                )).addBeans(
            createKeyServiceBean(),
            createIdentetyStoreHandlerBean(),
            createHasherBean()
    ).activate(RequestScoped.class).build();

    @BeforeAll
    static void setEnv() {
        System.setProperty("jwt.cert.file", "target/test_data/jwtkeys.ser");
    }


    // -- bean creators -- //

    public Bean<?> createHasherBean() {
        return MockBean.builder().types(PasswordHash.class).scope(ApplicationScoped.class)
                       .create(creationalContext -> activePasswordHashMock).build();
    }

    public Bean<?> createIdentetyStoreHandlerBean() {
        return MockBean.builder().types(IdentityStoreHandler.class).scope(ApplicationScoped.class)
                       .create(creationalContext -> activeIdentityStoreMock).build();
    }

    public Bean<?> createKeyServiceBean() {
        return MockBean.builder().types(KeyService.class).scope(ApplicationScoped.class)
                       .create(creationalContext -> activeKeyServiceMock).build();
    }


    //  -- directly injected in class -- //
    private KeyService activeKeyServiceMock;
    private IdentityStoreHandler activeIdentityStoreMock;
    private PasswordHash activePasswordHashMock;
    private EntityManager activeEntityManagerMock;

    //  -- tmp mocks for validation -- //
    private TypedQuery activeGetUserByPrincipalMock = Mockito.mock(TypedQuery.class);

    private TypedQuery activeNumWithPrincipalMock = Mockito.mock(TypedQuery.class);


    final String USER_PRINCIPAL = "test@test.test";
    final String USER_PASSWORD = "abc";
    final String USER_PASSWORD_HASHED = "HASHED_PASS";
    final long USER_ID = 1337;
    final List<String> USER_GROUPS_LIST = List
            .of(Group.USER_GROUP_NAME, Group.BUYER_GROUP_NAME, Group.CONTAINER_GROUP_NAME);
    final Set<String> USER_GROUPS = Set.of("TEST1", "TEST2", "TEST3");

    final String USED_USERNAME = "bipbop";


    AuthenticatedUser defaultAuthUser;

    @BeforeEach
    void buildMocks() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setPrincipalName(USER_PRINCIPAL);
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setGroups(USER_GROUPS.stream().map(Group::new).collect(Collectors.toList()));

        defaultAuthUser = authenticatedUser;

        // -- DATA QUERYs -- //
        TypedQuery<AuthenticatedUser> userByPrincipalQ = Mockito.mock(TypedQuery.class);
        Mockito.when(userByPrincipalQ.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(userByPrincipalQ);
        Mockito.when(userByPrincipalQ.getSingleResult()).thenReturn(defaultAuthUser);

        activeGetUserByPrincipalMock = userByPrincipalQ;

        TypedQuery<Long> numWithPrincipalQ = Mockito.mock(TypedQuery.class);
        Mockito.when(numWithPrincipalQ.setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL)))
               .thenReturn(numWithPrincipalQ);

        Mockito.when(numWithPrincipalQ.getSingleResult()).thenReturn(Long.valueOf(0));


        activeNumWithPrincipalMock = numWithPrincipalQ;


        // -- Entity Manager -- //
        EntityManager entityManagerMock = Mockito.mock(EntityManager.class);

        Mockito.when(entityManagerMock
                             .createQuery(AuthenticationService.GET_USER_BY_PRINCIPAL_QUERY, AuthenticatedUser.class))
               .thenReturn(userByPrincipalQ);

        Mockito.when(entityManagerMock.createQuery(AuthenticationService.GET_NUM_WITH_PRINCIPAL_QUERY, Long.class))
               .thenReturn(numWithPrincipalQ);

        Mockito.when(entityManagerMock.find(Mockito.eq(Group.class), Mockito.anyString())).thenAnswer(invocation -> {
            String groupName = invocation.getArgument(1, String.class);
            return new Group(groupName);
        });


        Mockito.when(entityManagerMock.find(Mockito.eq(AuthenticatedUser.class), Mockito.eq(USER_ID)))
               .thenReturn(defaultAuthUser);
        Mockito.when(entityManagerMock.find(Mockito.eq(AuthenticatedUser.class), Mockito.eq(1L))).thenReturn(null);

        activeEntityManagerMock = entityManagerMock;
        weld.select(AuthenticationService.class).get().setEntityManager(activeEntityManagerMock);

        // -- KEY SERVICE -- //

        KeyService keyServiceMock = Mockito.mock(KeyService.class);
        Mockito.when(keyServiceMock.generateNewJwtToken(Mockito.anyString(), Mockito.anyLong(), Mockito.anySet()))
               .thenReturn("hello").getMock();
        this.activeKeyServiceMock = keyServiceMock;

        // -- identity Store Handler -- //
        CredentialValidationResult validationResultOKMock = Mockito.mock(CredentialValidationResult.class);
        Mockito.when(validationResultOKMock.getCallerGroups()).thenReturn(USER_GROUPS);
        Mockito.when(validationResultOKMock.getStatus()).thenReturn(CredentialValidationResult.Status.VALID);

        CredentialValidationResult validationResultWrongMock = Mockito.mock(CredentialValidationResult.class);
        Mockito.when(validationResultWrongMock.getStatus()).thenReturn(CredentialValidationResult.Status.NOT_VALIDATED);

        IdentityStoreHandler identityStoreMock = Mockito.mock(IdentityStoreHandler.class);
        Mockito.when(identityStoreMock.validate(Mockito.any(UsernamePasswordCredential.class)))
               .thenAnswer(invocation -> {
                   UsernamePasswordCredential usernamePasswordCredential = invocation
                           .getArgument(0, UsernamePasswordCredential.class);

                   if (usernamePasswordCredential.getPassword().compareTo(USER_PASSWORD) && usernamePasswordCredential
                           .getCaller().equals(String.valueOf(USER_ID))) {
                       return validationResultOKMock;
                   } else {
                       return validationResultWrongMock;
                   }
               });

        this.activeIdentityStoreMock = identityStoreMock;

        // -- Password hasher -- //
        PasswordHash passwordHashMock = Mockito.mock(PasswordHash.class);

        Mockito.when(passwordHashMock.generate(Mockito.any())).thenReturn(USER_PASSWORD_HASHED);

        activePasswordHashMock = passwordHashMock;


    }


    @Test
    void getUserFromPrincipal() {


        // positive test
        Optional<AuthenticatedUser> testAuthUser = weld.select(AuthenticationService.class).get()
                                                       .getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(activeGetUserByPrincipalMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isPresent());

        Mockito.reset(activeGetUserByPrincipalMock);
        Mockito.when(activeGetUserByPrincipalMock.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(activeGetUserByPrincipalMock);
        Mockito.when(activeGetUserByPrincipalMock.getSingleResult()).thenThrow(NoResultException.class);

        testAuthUser = weld.select(AuthenticationService.class).get().getUserFromPrincipal(USER_PRINCIPAL);

        Mockito.verify(activeGetUserByPrincipalMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(testAuthUser.isEmpty());

    }

    @Test
    void getToken() {
        UsernamePasswordData usernamePasswordData      = new UsernamePasswordData(USER_PASSWORD, USER_PRINCIPAL);
        UsernamePasswordData wrongUsernamePasswordData = new UsernamePasswordData(USER_PASSWORD, "AAA");


        Optional<String> positiveTest = weld.select(AuthenticationService.class).get().getToken(usernamePasswordData);
        Mockito.verify(activeGetUserByPrincipalMock).setParameter(Mockito.eq("pname"), Mockito.eq(USER_PRINCIPAL));
        assertTrue(positiveTest.isPresent());
        assertEquals(positiveTest.get(), "hello");


        Mockito.reset(activeGetUserByPrincipalMock);
        Mockito.when(activeGetUserByPrincipalMock.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(activeGetUserByPrincipalMock);
        Mockito.when(activeGetUserByPrincipalMock.getSingleResult()).thenThrow(NoResultException.class);

        Optional<String> negativeTest = weld.select(AuthenticationService.class).get()
                                            .getToken(wrongUsernamePasswordData);


        assertTrue(negativeTest.isEmpty());

    }

    @Test
    void getUserFromId() {
        Optional<AuthenticatedUser> positiveTest = weld.select(AuthenticationService.class).get()
                                                       .getUserFromId(USER_ID);
        assertTrue(positiveTest.isPresent());
        assertEquals(positiveTest.get().getId(), USER_ID);

        Optional<AuthenticatedUser> negativeTest = weld.select(AuthenticationService.class).get().getUserFromId(1);

        assertTrue(negativeTest.isEmpty());
    }

    private void setJwtValue(String newVal) {
        weld.select(AuthenticationService.class).get()
            .setJwtSubject(Mockito.when(Mockito.mock(Instance.class).get())
                                  .thenReturn(Optional.ofNullable(newVal))
                                  .getMock());
    }

    @Test
    void getCurrentAuthUser() {
        setJwtValue(String.valueOf(USER_ID));

        Optional<AuthenticatedUser> authenticatedUser = weld.select(AuthenticationService.class).get()
                                                            .getCurrentAuthUser();

        assertTrue(authenticatedUser.isPresent());
        assertEquals(authenticatedUser.get().getId(), USER_ID);

        setJwtValue(null);
        Optional<AuthenticatedUser> negativeTest = weld.select(AuthenticationService.class).get().getCurrentAuthUser();

        assertTrue(negativeTest.isEmpty());
    }

    @Test
    void isPrincipalInUse() {

        boolean isInUse = weld.select(AuthenticationService.class).get().isPrincipalInUse(USER_PRINCIPAL);
        assertFalse(isInUse);

        // principal is in use
        Mockito.reset(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.getSingleResult()).thenReturn(22L);

        isInUse = weld.select(AuthenticationService.class).get().isPrincipalInUse(USED_USERNAME);
        assertTrue(isInUse);

        // error in fetch
        Mockito.reset(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.getSingleResult()).thenThrow(NoResultException.class);

        isInUse = weld.select(AuthenticationService.class).get().isPrincipalInUse(USER_PRINCIPAL);
        assertTrue(isInUse);


    }

    @Test
    void createUser() {
        NewAuthUserData newAuthUserData = new NewAuthUserData();
        newAuthUserData.setUserName(USER_PRINCIPAL);
        newAuthUserData.setPassword(USER_PASSWORD);
        newAuthUserData.setGroups(USER_GROUPS_LIST);

        // ok test
        Optional<AuthenticatedUser> authenticatedUserOptional = weld.select(AuthenticationService.class).get()
                                                                    .createUser(newAuthUserData);
        assertTrue(authenticatedUserOptional.isPresent());
        AuthenticatedUser authenticatedUser = authenticatedUserOptional.get();

        assertEquals(newAuthUserData.getUserName(), authenticatedUser.getPrincipalName());
        assertEquals(USER_PASSWORD_HASHED, authenticatedUser.getPassword());

        List<String> authUserGroupNames = authenticatedUser.getGroups().stream().map(Group::getName)
                                                           .collect(Collectors.toList());
        Assertions.assertTrue(USER_GROUPS_LIST.containsAll(authUserGroupNames));
        Assertions.assertTrue(authUserGroupNames.containsAll(USER_GROUPS_LIST));
        Assertions.assertEquals(USER_GROUPS_LIST.size(), authUserGroupNames.size());// technically unececery

        // invalid group test
        newAuthUserData.setGroups(List.of("invalid group name"));

        authenticatedUserOptional = weld.select(AuthenticationService.class).get().createUser(newAuthUserData);
        assertTrue(authenticatedUserOptional.isEmpty());

        // username is in use
        Mockito.reset(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.setParameter(Mockito.eq("pname"), Mockito.anyString()))
               .thenReturn(activeNumWithPrincipalMock);
        Mockito.when(activeNumWithPrincipalMock.getSingleResult()).thenReturn(22L);

        newAuthUserData.setUserName(USED_USERNAME);
        authenticatedUserOptional = weld.select(AuthenticationService.class).get().createUser(newAuthUserData);
        assertTrue(authenticatedUserOptional.isEmpty());
    }

    @Test
    void changePassword() {
        setJwtValue(String.valueOf(USER_ID));

        defaultAuthUser.setPassword("NOT hashed");

        boolean passwordDidChanged = weld.select(AuthenticationService.class).get()
                                         .changePassword("passwd", USER_PASSWORD);

        assertTrue(passwordDidChanged);
        assertEquals(defaultAuthUser.getPassword(), USER_PASSWORD_HASHED);

        // negative test
        defaultAuthUser.setPassword("NOT hashed");

        passwordDidChanged = weld.select(AuthenticationService.class).get()
                                 .changePassword("passwd", "invalid password");

        assertFalse(passwordDidChanged);
        assertEquals(defaultAuthUser.getPassword(), "NOT hashed");


    }
}