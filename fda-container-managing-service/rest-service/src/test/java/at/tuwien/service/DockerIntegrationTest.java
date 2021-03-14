package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.dto.database.DatabaseContainerCreateDto;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DockerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private DockerClient dockerClient;

    @BeforeEach
    public void beforeEach() {
        final List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .stream()
                .filter(c -> !c.getImage().startsWith("fda"))
                .collect(Collectors.toList());
        containers.stream()
                .filter(c -> c.getState().equals("running"))
                .forEach(container -> {
                    log.debug("Stopping container {}", container.getId());
                    dockerClient.stopContainerCmd(container.getId()).exec();
                });
        containers.forEach(container -> {
            dockerClient.removeContainerCmd(container.getId()).exec();
        });
    }

    @Test
    public void create_succeeds() throws ImageNotFoundException {
        when(imageRepository.findByImage(IMAGE_1_REPOSITORY, IMAGE_1_TAG))
                .thenReturn(IMAGE_1);
        DatabaseContainerCreateDto dto = new DatabaseContainerCreateDto();
        dto.setImage(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG);
        dto.setContainerName(CONTAINER_1_NAME);
        dto.setDatabaseName(CONTAINER_1_DATABASE);

        final DatabaseContainer response = containerService.create(dto);
        Assertions.assertEquals(CONTAINER_1_ID, response.getContainerId());
        Assertions.assertEquals(CONTAINER_1_DATABASE, response.getDatabaseName());
    }

    @Test
    public void create_noImage_fails() {
        final DatabaseContainerCreateDto containerDto = new DatabaseContainerCreateDto();

        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
    }

    @Test
    public void create_imageNotFound_fails() {
        final DatabaseContainerCreateDto containerDto = new DatabaseContainerCreateDto();
        containerDto.setImage("postgres:latest");

        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
    }

    @Test
    public void stop_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.stop(CONTAINER_1_ID));
    }

    @Test
    public void remove_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.remove(CONTAINER_1_ID));
    }

    @Test
    public void start_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.start(CONTAINER_1_ID));
    }
}
