package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Persistence Layer Tests
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

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
        imageRepository.save(IMAGE_1);

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
        imageRepository.save(IMAGE_1);
        containerRepository.save(CONTAINER_1);

        /* test */
        imageService.delete(IMAGE_1_ID);
        assertTrue(imageRepository.findById(IMAGE_1_ID).isEmpty());
        assertFalse(containerRepository.findById(CONTAINER_1_ID).isPresent());
    }

}
