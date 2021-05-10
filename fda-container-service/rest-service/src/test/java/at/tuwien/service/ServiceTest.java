package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.container.*;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ContainerStillRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceTest extends BaseIntegrationTest {

    @Autowired
    private ContainerService containerService;

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private DockerClient dockerClient;

    @Test
    public void listAllDatabases_succeeds() {
        when(containerRepository.findAll())
                .thenReturn(List.of(CONTAINER_1, CONTAINER_2));

        final List<Container> response = containerService.getAll();

        /* test */
        assertEquals(2, response.size());
        assertEquals(CONTAINER_1_ID, response.get(0).getId());
        assertEquals(CONTAINER_1_NAME, response.get(0).getName());
        assertEquals(CONTAINER_2_ID, response.get(1).getId());
        assertEquals(CONTAINER_2_NAME, response.get(1).getName());
    }

    @Disabled(value = "cannot test docker api")
    @Test
    public void create_succeeds() {
        // cannot test
    }

    @Test
    public void create_noImage_fails() {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(imageRepository.findByRepositoryAndTag(CONTAINER_1_IMAGE.getRepository(), CONTAINER_1_IMAGE.getTag()))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            containerService.create(request);
        });
    }

    @Test
    public void create_docker_fails() {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(imageRepository.findByRepositoryAndTag(CONTAINER_1_IMAGE.getRepository(), CONTAINER_1_IMAGE.getTag()))
                .thenReturn(Optional.of(CONTAINER_1_IMAGE));
        when(dockerClient.createContainerCmd(any()))
                .thenThrow(ConflictException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.create(request);
        });
    }

    @Test
    public void findById_succeeds() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));

        final Container response = containerService.getById(CONTAINER_1_ID);

        /* test */
        assertEquals(CONTAINER_1_ID, response.getId());
        assertEquals(CONTAINER_1_NAME, response.getName());
    }

    @Test
    public void findById_notFound_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.getById(CONTAINER_1_ID);
        });
    }

    @Disabled(value = "cannot test docker api")
    @Test
    public void findById_docker_fails() {
        // cannot test
    }

    @Disabled(value = "cannot test docker api")
    @Test
    public void change_start_succeeds() {
        // cannot test
    }

    @Disabled(value = "cannot test docker api")
    @Test
    public void change_stop_succeeds() {
        // cannot test
    }

    @Test
    public void change_start_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.startContainerCmd(CONTAINER_1_HASH))
                .thenThrow(NotFoundException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.start(CONTAINER_1_ID);
        });
    }

    @Test
    public void change_stop_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.stopContainerCmd(CONTAINER_1_HASH))
                .thenThrow(NotFoundException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.stop(CONTAINER_1_ID);
        });
    }

    @Test
    public void change_stop_noContainer_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.stop(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_noContainer_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.removeContainerCmd(CONTAINER_1_HASH))
                .thenThrow(NotModifiedException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_docker_fails2() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.removeContainerCmd(CONTAINER_1_HASH))
                .thenThrow(NotFoundException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_dockerStillRunning_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.removeContainerCmd(CONTAINER_1_HASH))
                .thenThrow(ConflictException.class);

        /* test */
        assertThrows(ContainerStillRunningException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }
}
