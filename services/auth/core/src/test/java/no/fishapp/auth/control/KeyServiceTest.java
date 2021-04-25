package no.fishapp.auth.control;

import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;

import static org.junit.jupiter.api.Assertions.*;

@EnableWeld
class KeyServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(KeyService.class).addBeans(createAAABean()).build();

    static Bean<?> createAAABean() {
        return MockBean.builder().types(AAA.class).scope(ApplicationScoped.class).creating(
                Mockito.when(Mockito.mock(AAA.class).bbb()).thenReturn("LALA").getMock()).build();
    }

    @Test
    void testP(KeyService keyService) {
        Assertions.assertEquals("LALA", keyService.testP());
        Assertions.assertTrue(true);
        System.out.println(keyService.testP());

    }
}