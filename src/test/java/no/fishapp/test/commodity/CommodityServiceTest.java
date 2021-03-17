package no.fishapp.test.commodity;
import manifold.ext.rt.api.Jailbreak;
import no.fishapp.app.commodity.control.CommodityService;
import no.fishapp.app.commodity.entity.Commodity;
import no.fishapp.app.resources.entity.Image;
import no.fishapp.app.util.ImageUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
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
public class CommodityServiceTest {

    @Mock EntityManager em;
    @Mock ImageUtil iu;
    //@Mock File file;
    @Jailbreak @InjectMocks
    CommodityService commodityService;
    private String name;
    private FormDataMultiPart photo;
    private Image image;
    private List<Image> list;
    private File file;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
//        commodityService = new CommodityService();
//        commodityService.entityManager = mock(EntityManager.class);
//        commodityService.imageUtil = mock(ImageUtil.class);
        //file = mock(File.class);
        image = new Image();
        list = new ArrayList<>();
        list.add(image);
        name = "TestTest";
        photo = new FormDataMultiPart();
        file = new File(tempDir, "photo.png");
    }

    @AfterEach
    public void tearDown() {}


    @Test
    public void addNewCommodityTest() throws IOException {
        when(iu.saveImages(photo, file, "image"))
                .thenReturn(list);
        Commodity com = commodityService.addNewCommodity(name, photo);
        assertEquals(name, com.getName());
        //verify(commodityService.entityManager, times(1)).persist(com);
    }
}