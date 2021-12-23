package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.*;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.ContainerEndpoint;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.service.ContainerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private ContainerService containerService;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ContainerEndpoint containerEndpoint;

    @Test
    public void listAllDatabases_succeeds() {
        when(containerService.getAll())
                .thenReturn(List.of(CONTAINER_1));

        final ResponseEntity<List<ContainerBriefDto>> response = containerEndpoint.findAll();

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws ImageNotFoundException, DockerClientException, ContainerNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .build();
        when(containerService.create(request))
                .thenReturn(CONTAINER_1);

        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.create(request);

        /* test */
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(CONTAINER_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Disabled
    @Test
    public void create_noImage_fails() throws DockerClientException, ImageNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository("image")
                .tag("notexisting")
                .build();
        when(imageRepository.findByRepositoryAndTag(request.getRepository(), request.getTag()))
                .thenReturn(Optional.empty());

        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.create(request);

        /* test */
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Disabled
    @Test
    public void create_docker_fails() throws DockerClientException, ImageNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .build();
        when(imageRepository.findByRepositoryAndTag(request.getRepository(), request.getTag()))
                .thenReturn(Optional.of(IMAGE_1));

        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.create(request);

        /* test */
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findById_succeeds() throws ContainerNotFoundException, DockerClientException {
        when(containerService.getById(CONTAINER_1_ID))
                .thenReturn(CONTAINER_1);
        when(containerService.getContainerState(CONTAINER_1_HASH))
                .thenReturn(ContainerStateDto.RUNNING);

        /* test */
        final ResponseEntity<ContainerDto> response = containerEndpoint.findById(CONTAINER_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void findById_notFound_fails() throws ContainerNotFoundException {
        given(containerService.getById(CONTAINER_1_ID))
                .willAnswer(invocation -> {
                    throw new ContainerNotFoundException("no container");
                });

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerEndpoint.findById(CONTAINER_1_ID);
        });
    }

    @Test
    public void findById_docker_fails() throws ContainerNotFoundException, DockerClientException {
        given(containerService.getById(CONTAINER_1_ID))
                .willAnswer(invocation -> {
                    throw new DockerClientException("no state");
                });
        when(containerService.getContainerState(CONTAINER_1_HASH))
                .thenReturn(null);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.findById(CONTAINER_1_ID);
        });
    }

    @Test
    public void change_start_succeeds() throws DockerClientException, ContainerNotFoundException, ContainerStillRunningException {
        final ContainerChangeDto request = ContainerChangeDto.builder()
                .action(ContainerActionTypeDto.START)
                .build();
        when(containerService.start(CONTAINER_1_ID))
                .thenReturn(CONTAINER_1);

        /* test */
        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.modify(CONTAINER_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(CONTAINER_1_ID, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void change_stop_succeeds() throws DockerClientException, ContainerNotFoundException, ContainerStillRunningException {
        final ContainerChangeDto request = ContainerChangeDto.builder()
                .action(ContainerActionTypeDto.STOP)
                .build();
        when(containerService.stop(CONTAINER_1_ID))
                .thenReturn(CONTAINER_1);

        /* test */
        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.modify(CONTAINER_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(CONTAINER_1_ID, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void change_start_docker_fails() throws DockerClientException, ContainerNotFoundException {
        final ContainerChangeDto request = ContainerChangeDto.builder()
                .action(ContainerActionTypeDto.START)
                .build();
        when(containerService.start(CONTAINER_1_ID))
                .thenThrow(DockerClientException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.modify(CONTAINER_1_ID, request);
        });
    }

    @Test
    public void change_stop_docker_fails() throws DockerClientException, ContainerNotFoundException {
        final ContainerChangeDto request = ContainerChangeDto.builder()
                .action(ContainerActionTypeDto.STOP)
                .build();
        when(containerService.stop(CONTAINER_1_ID))
                .thenThrow(DockerClientException.class);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.modify(CONTAINER_1_ID, request);
        });
    }

    @Test
    public void change_stop_noContainer_fails() throws DockerClientException, ContainerNotFoundException {
        final ContainerChangeDto request = ContainerChangeDto.builder()
                .action(ContainerActionTypeDto.STOP)
                .build();
        when(containerService.stop(CONTAINER_1_ID))
                .thenThrow(ContainerNotFoundException.class);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerEndpoint.modify(CONTAINER_1_ID, request);
        });
    }

    @Test
    public void delete_noContainer_fails() throws ContainerStillRunningException, DockerClientException, ContainerNotFoundException {
        doThrow(new ContainerNotFoundException("no container"))
                .when(containerService)
                .remove(CONTAINER_1_ID);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerEndpoint.delete(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_success() throws DockerClientException {
        doNothing()
                .when(containerService)
                .remove(CONTAINER_1_ID);

        /* test */
        final ResponseEntity<?> response = containerEndpoint.delete(CONTAINER_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void delete_docker_fails() throws ContainerStillRunningException, DockerClientException, ContainerNotFoundException {
        doThrow(new DockerClientException("docker failed"))
                .when(containerService)
                .remove(CONTAINER_1_ID);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.delete(CONTAINER_1_ID);
        });
    }

    @Test
    public void delete_dockerStillRunning_fails() throws ContainerStillRunningException, DockerClientException, ContainerNotFoundException {
        doThrow(new ContainerStillRunningException("container running"))
                .when(containerService)
                .remove(CONTAINER_1_ID);

        /* test */
        assertThrows(ContainerStillRunningException.class, () -> {
            containerEndpoint.delete(CONTAINER_1_ID);
        });
    }
}
