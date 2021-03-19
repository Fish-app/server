package no.fishapp.test.commodity;
import manifold.ext.rt.api.Jailbreak;
import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.resources.entity.Image;
import no.fishapp.app.util.ImageUtil;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommodityServiceTest {

    @Mock
    EntityManager entityManager;
    @Mock
    ImageUtil imageUtil;

    @Jailbreak @InjectMocks
    CommodityService commodityService;

    @BeforeEach
    void setUp() {
        commodityService.photoSaveDir = "/images";
    }

    @AfterEach
    void tearDown() {}

    /**
     * Tests that a new {@link Commodity} gets added with the provided name and image.
     * @throws IOException This does not happen because of the mocks
     */
    @Test
    void addNewCommodityTest() throws IOException {
        Image image = new Image();
        List<Image> list = new ArrayList<>();
        list.add(image);
        String name = "TestTest";
        FormDataMultiPart photo = new FormDataMultiPart();
        when(imageUtil.saveImages(any(FormDataMultiPart.class), any(File.class), anyString()))
                .thenReturn(list);
        Commodity com = commodityService.addNewCommodity(name, photo);
        assertEquals(name, com.getName());
        assertEquals(image, com.getCommodityImage());
        verify(entityManager, times(2)).persist(any());
    }


    /**
     * Test that the {@link EntityManager} only once tries to get the {@link Commodity}
     */
    @Test
    void getCommodityTest() {
        commodityService.getCommodity(1L);
        verify(entityManager, times(1)).find(any(), anyLong());
    }
}