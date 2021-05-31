package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerStateDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.PortBinding;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
