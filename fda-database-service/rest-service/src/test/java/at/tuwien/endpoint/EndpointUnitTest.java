package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.ContainerDatabaseEndpoint;
import at.tuwien.exception.*;
import at.tuwien.service.impl.MariaDbServiceImpl;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Network;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private MariaDbServiceImpl databaseService;

    @Autowired
    private ContainerDatabaseEndpoint databaseEndpoint;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        afterAll();
        /* create networks */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        dockerClient.createNetworkCmd()
                .withName("fda-public")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.29.0.0/16")))
                .withEnableIpv6(false)
                .exec();

        /* create amqp */
        final CreateContainerResponse request = dockerClient.createContainerCmd(BROKER_IMAGE + ":" + BROKER_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(BROKER_NAME)
                .withIpv4Address(BROKER_IP)
                .withHostName(BROKER_HOSTNAME)
                .exec();
        dockerClient.startContainerCmd(request.getId())
                .exec();
        Thread.sleep(12 * 1000);
    }

    @AfterAll
    public static void afterAll() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    log.info("Delete container {}", Arrays.asList(container.getNames()));
                    try {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    } catch (NotModifiedException e) {
                        // ignore
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                });
        /* remove networks */
        dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .filter(n -> n.getName().startsWith("fda"))
                .forEach(network -> {
                    log.info("Delete network {}", network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @Test
    public void findAll_succeeds() {
        when(databaseService.findAll(CONTAINER_1_ID))
                .thenReturn(List.of(DATABASE_1));

        final ResponseEntity<List<DatabaseBriefDto>> response = databaseEndpoint.findAll(CONTAINER_1_ID);

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, UserNotFoundException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .description(DATABASE_1_DESCRIPTION)
                .build();
        when(databaseService.create(CONTAINER_1_ID, request))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseDto> response = databaseEndpoint.create(CONTAINER_1_ID, request);

        /* test */
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    @Disabled
    @WithMockUser(username = "not3xisting", roles = {"ROLE_RESEARCHER"})
    public void create_notAuthenticated_fails() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, UserNotFoundException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .description(DATABASE_1_DESCRIPTION)
                .build();
        when(databaseService.create(CONTAINER_1_ID, request))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseDto> response = databaseEndpoint.create(CONTAINER_1_ID, request);

        /* test */
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void create_containerNotFound_fails() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, UserNotFoundException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .description(DATABASE_1_DESCRIPTION)
                .build();

        /* test */
        when(databaseService.create(CONTAINER_1_ID, request))
                .thenThrow(ContainerNotFoundException.class);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseEndpoint.create(CONTAINER_1_ID, request);
        });
    }

    @Test
    public void create_imageNotSupported_fails() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, UserNotFoundException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .description(DATABASE_1_DESCRIPTION)
                .build();

        when(databaseService.create(CONTAINER_1_ID, request))
                .thenThrow(ImageNotSupportedException.class);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.create(CONTAINER_1_ID, request);
        });
    }

    @Test
    public void findById_succeeds() throws DatabaseNotFoundException {
        when(databaseService.findById(CONTAINER_1_ID, DATABASE_1_ID))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseDto> response = databaseEndpoint.findById(CONTAINER_1_ID, DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void findById_notFound_fails() throws DatabaseNotFoundException {
        when(databaseService.findById(CONTAINER_1_ID, DATABASE_1_ID))
                .thenThrow(DatabaseNotFoundException.class);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.findById(CONTAINER_1_ID, DATABASE_1_ID);
        });
    }

    @Test
    public void delete_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException {
        final ResponseEntity<?> response = databaseEndpoint.delete(CONTAINER_1_ID, DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void delete_invalidImage_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException {
        willThrow(ImageNotSupportedException.class)
                .given(databaseService)
                .delete(CONTAINER_1_ID, DATABASE_1_ID);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.delete(CONTAINER_1_ID, DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException {
        willThrow(DatabaseNotFoundException.class)
                .given(databaseService)
                .delete(CONTAINER_1_ID, DATABASE_1_ID);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.delete(CONTAINER_1_ID, DATABASE_1_ID);
        });
    }

}
