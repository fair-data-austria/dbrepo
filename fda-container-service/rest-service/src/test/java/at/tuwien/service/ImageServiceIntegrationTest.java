package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static at.tuwien.BaseUnitTest.IMAGE_1_ENV_DTO;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private ImageService imageService;

    @Test
    public void test_notFound_fails() {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository("s0m3th1ng_n0t3x1st1ng")
                .tag("d3v_h3ll")
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .build();

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageService.create(request);
        });
    }

}
