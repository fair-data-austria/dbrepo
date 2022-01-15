package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.*;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ContainerStillRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.service.impl.ContainerServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ContainerServiceImpl containerService;

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private DockerClient dockerClient;

    @Test
    public void listAllDatabases_succeeds() {
        when(containerRepository.findAll())
                .thenReturn(List.of(CONTAINER_1));

        final List<Container> response = containerService.getAll();

        /* test */
        assertEquals(1, response.size());
        assertEquals(CONTAINER_1_ID, response.get(0).getId());
        assertEquals(CONTAINER_1_NAME, response.get(0).getName());
    }

    @Test
    public void create_noImage_fails() {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(IMAGE_1.getRepository())
                .tag(IMAGE_1.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(imageRepository.findByRepositoryAndTag(IMAGE_1.getRepository(), IMAGE_1.getTag()))
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
                .repository(IMAGE_1.getRepository())
                .tag(IMAGE_1.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(imageRepository.findByRepositoryAndTag(IMAGE_1.getRepository(), IMAGE_1.getTag()))
                .thenReturn(Optional.of(IMAGE_1));
        when(dockerClient.createContainerCmd(any()))
                .thenThrow(ConflictException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.create(request);
        });
    }

    @Test
    public void findById_succeeds() throws ContainerNotFoundException {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));

        final Container response = containerService.find(CONTAINER_1_ID);

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
            containerService.find(CONTAINER_1_ID);
        });
    }

    @Disabled("cannot mock abstract method")
    @Test
    public void change_start_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        doAnswer(invocation -> new NotFoundException("not found"))
                .when(dockerClient)
                .startContainerCmd(CONTAINER_1_HASH);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.start(CONTAINER_1_ID);
        });
    }

    @Disabled("cannot mock abstract method")
    @Test
    public void change_stop_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        doAnswer(invocation -> new NotFoundException("docker failed"))
                .when(dockerClient)
                .stopContainerCmd(CONTAINER_1_HASH);

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

    @Disabled("cannot mock abstract method")
    @Test
    public void delete_docker_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        doAnswer(invocation -> new NotModifiedException("not modified")).
                when(dockerClient)
                .removeContainerCmd(CONTAINER_1_HASH);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

    @Disabled("cannot mock abstract method")
    @Test
    public void delete_docker_fails2() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.removeContainerCmd(CONTAINER_1_HASH))
                .thenCallRealMethod();
        doAnswer(invocation -> new NotFoundException("not found in docker"))
                .when(dockerClient)
                .startContainerCmd(CONTAINER_1_HASH)
                .exec();

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

    @Disabled("cannot mock abstract method")
    @Test
    public void delete_dockerStillRunning_fails() {
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(dockerClient.startContainerCmd(CONTAINER_1_HASH))
                .thenCallRealMethod();
        doAnswer(invocation -> new ConflictException("running"))
                .when(dockerClient)
                .startContainerCmd(CONTAINER_1_HASH)
                .exec();

        /* test */
        assertThrows(ContainerStillRunningException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }
}
