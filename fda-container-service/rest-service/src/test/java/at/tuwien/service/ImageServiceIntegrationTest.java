package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Transactional
    @BeforeEach
    public void beforeEach() {
        containerRepository.save(CONTAINER_1);
    }

    @Test
    public void create_notFound_fails() {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository("s0m3th1ng_n0t3x1st1ng")
                .tag("d3v_h3ll")
                .dialect(IMAGE_1_DIALECT)
                .driverClass(IMAGE_1_DRIVER)
                .jdbcMethod(IMAGE_1_JDBC)
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .logo(IMAGE_1_LOGO)
                .build();

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageService.create(request);
        });
    }

    @Test
    public void create_duplicate_fails() {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .defaultPort(IMAGE_1_PORT)
                .driverClass(IMAGE_1_DRIVER)
                .jdbcMethod(IMAGE_1_JDBC)
                .dialect(IMAGE_1_DIALECT)
                .environment(IMAGE_1_ENV_DTO)
                .logo(IMAGE_1_LOGO)
                .build();

        /* test */
        assertThrows(ImageAlreadyExistsException.class, () -> {
            imageService.create(request);
        });
    }

    @Test
    public void delete_hasContainer_succeeds() throws ImageNotFoundException, PersistenceException {

        /* test */
        imageService.delete(IMAGE_1_ID);
        assertTrue(imageRepository.findById(IMAGE_1_ID).isEmpty());
        assertTrue(containerRepository.findById(CONTAINER_1_ID).isPresent()); /* container should NEVER be deletable in the metadata db */
    }

    @Test
    public void delete_noContainer_succeeds() throws ImageNotFoundException, PersistenceException {
        imageRepository.save(IMAGE_2);

        /* test */
        imageService.delete(IMAGE_2_ID);
        assertTrue(imageRepository.findById(IMAGE_2_ID).isEmpty());
    }

}
