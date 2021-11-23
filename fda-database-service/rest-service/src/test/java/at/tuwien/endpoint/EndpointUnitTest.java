package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.DatabaseEndpoint;
import at.tuwien.exception.*;
import at.tuwien.service.DatabaseService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.channels.Channel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private DatabaseService databaseService;

    @Autowired
    private DatabaseEndpoint databaseEndpoint;

    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        afterAll();
        final DockerConfig dockerConfig = new DockerConfig();
        final HostConfig hostConfig = dockerConfig.hostConfig();
        final DockerClient dockerClient = dockerConfig.dockerClientConfiguration();
        /* create network */
        final boolean exists = (long) dockerClient.listNetworksCmd()
                .withNameFilter("fda-public")
                .exec()
                .size() == 1;
        if (!exists) {
            dockerClient.createNetworkCmd()
                    .withName("fda-public")
                    .withInternal(true)
                    .withIpam(new Network.Ipam()
                            .withConfig(new Network.Ipam.Config()
                                    .withSubnet("172.29.0.0/16")))
                    .withEnableIpv6(false)
                    .exec();
        }
        /* create amqp */
        final CreateContainerResponse request = dockerClient.createContainerCmd(BROKER_IMAGE + ":" + BROKER_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(BROKER_NAME)
                .withIpv4Address(BROKER_IP)
                .withHostName(BROKER_HOSTNAME)
                .exec();
        dockerClient.startContainerCmd(request.getId()).exec();
        Thread.sleep(5 * 1000);
    }

    @AfterAll
    public static void afterAll() {
        final DockerConfig dockerConfig = new DockerConfig();
        final DockerClient dockerClient = dockerConfig.dockerClientConfiguration();
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    System.out.println("DELETE CONTAINER " + Arrays.toString(container.getNames()));
                    try {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    } catch (NotModifiedException e) {
                        // ignore
                    }
                    try {
                        dockerClient.removeContainerCmd(container.getId()).exec();
                    } catch (ConflictException e) {
                        // ignore
                    }
                });
        /* remove networks */
        dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .filter(n -> n.getName().startsWith("fda"))
                .forEach(network -> {
                    System.out.println("DELETE NETWORK " + network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @Test
    public void findAll_succeeds() {
        when(databaseService.findAll())
                .thenReturn(List.of(DATABASE_1));

        final ResponseEntity<List<DatabaseBriefDto>> response = databaseEndpoint.findAll();

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();
        when(databaseService.create(request))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseBriefDto> response = databaseEndpoint.create(request);

        /* test */
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void create_containerNotFound_fails() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(ContainerNotFoundException.class);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void create_imageNotSupported_fails() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(ImageNotSupportedException.class);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void findById_succeeds() throws DatabaseNotFoundException {
        when(databaseService.findById(DATABASE_1_ID))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseDto> response = databaseEndpoint.findById(DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void findById_notFound_fails() throws DatabaseNotFoundException {
        when(databaseService.findById(DATABASE_1_ID))
                .thenThrow(DatabaseNotFoundException.class);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.findById(DATABASE_1_ID);
        });
    }

    @Test
    public void modify_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException {
        final DatabaseModifyDto request = DatabaseModifyDto.builder()
                .databaseId(DATABASE_1_ID)
                .name("NAME")
                .isPublic(true)
                .build();

        /* test */
        final ResponseEntity<DatabaseBriefDto> response = databaseEndpoint.modify(CONTAINER_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void modify_notFound_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException {
        final DatabaseModifyDto request = DatabaseModifyDto.builder()
                .databaseId(9999L)
                .build();

        /* test */
        final ResponseEntity<DatabaseBriefDto> response = databaseEndpoint.modify(CONTAINER_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void delete_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException {
        final ResponseEntity<?> response = databaseEndpoint.delete(DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void delete_invalidImage_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException {
        willThrow(ImageNotSupportedException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException {
        willThrow(DatabaseNotFoundException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }

}
