package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.api.dto.container.ContainerStateDto;
import at.tuwien.endpoints.ContainerEndpoint;
import at.tuwien.entity.Container;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ContainerStillRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.service.ContainerService;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointTest extends BaseIntegrationTest {

    @MockBean
    private ContainerService containerService;

    @Autowired
    private ContainerEndpoint containerEndpoint;

    @Test
    public void listAllDatabases_succeeds() {
        when(containerService.getAll())
                .thenReturn(List.of(CONTAINER_1, CONTAINER_2));

        final List<Container> response = containerService.getAll();

        assertEquals(2, response.size());
        assertEquals(CONTAINER_1_ID, response.get(0).getId());
        assertEquals(CONTAINER_1_NAME, response.get(0).getName());
        assertEquals(CONTAINER_2_ID, response.get(1).getId());
        assertEquals(CONTAINER_2_NAME, response.get(1).getName());
    }

    @Test
    public void create_succeeds() throws ImageNotFoundException, DockerClientException, ContainerNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(containerService.getById(CONTAINER_1_ID))
                .thenReturn(CONTAINER_1);
        when(containerService.getContainerState(CONTAINER_1_HASH))
                .thenReturn(ContainerStateDto.CREATED);
        when(containerService.findIpAddresses(CONTAINER_1_HASH))
                .thenReturn(Map.of("test", CONTAINER_1_IP));

        /* test */
        final ResponseEntity<ContainerBriefDto> response = containerEndpoint.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void create_noImage_fails() throws DockerClientException, ImageNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        given(containerService.create(request))
                .willAnswer(invocation -> { throw new ImageNotFoundException("no image"); });

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            containerEndpoint.create(request);
        });
    }

    @Test
    public void create_docker_fails() throws DockerClientException, ImageNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        given(containerService.create(request))
                .willAnswer(invocation -> { throw new DockerClientException("name already occupied"); });

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.create(request);
        });
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
        assertEquals(CONTAINER_1_ID, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void findById_notFound_fails() throws ContainerNotFoundException {
        given(containerService.getById(CONTAINER_1_ID))
                .willAnswer(invocation -> { throw new ContainerNotFoundException("no container"); });

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerEndpoint.findById(CONTAINER_1_ID);
        });
    }

    @Test
    public void findById_docker_fails() throws ContainerNotFoundException, DockerClientException {
        given(containerService.getById(CONTAINER_1_ID))
                .willAnswer(invocation -> { throw new DockerClientException("no state"); });
        when(containerService.getContainerState(CONTAINER_1_HASH))
                .thenReturn(null);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerEndpoint.findById(CONTAINER_1_ID);
        });
    }

    @Test
    public void change_succeeds() {
        //
    }

    @Test
    public void change_fails() {
        //
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
