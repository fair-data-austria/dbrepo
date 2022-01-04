package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.elastic.DatabaseidxRepository;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DatabaseServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private Channel channel;

    @MockBean
    private DatabaseidxRepository databaseidxRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JdbcConnector jdbcConnector;

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

        /* create mariadb */
        final CreateContainerResponse request2 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_NAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();

        /* start container */
        final Container container = Container.builder()
                .hash(request.getId())
                .build();
        CONTAINER_1.setHash(request2.getId());
        DockerConfig.startContainer(container);
        DockerConfig.startContainer(CONTAINER_1);
    }

    @Transactional
    @BeforeEach
    public void beforeEach() {
        imageRepository.save(IMAGE_1);
        DATABASE_1.setContainer(CONTAINER_1);
        databaseRepository.save(DATABASE_1);
    }

    @AfterAll
    public static void afterAll() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    log.info("Delete container {}", container.getNames()[0]);
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

        /* test */
        final List<Database> response = databaseService.findAll();
        assertEquals(1, response.size());
    }

    @Transactional
    @Test
    public void create_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, SQLException, AmqpException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID) /* we need this container */
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* test */
        final Database response = databaseService.create(request);
        assertEquals(DATABASE_1_NAME, response.getName());
        assertEquals(DATABASE_1_PUBLIC, response.getIsPublic());
        assertEquals(CONTAINER_1_ID, response.getContainer().getId());
        final DSLContext context = jdbcConnector.open(response);
        assertTrue(context.meta().getSchemas().size() > 0);
    }

    @Test
    public void create_notFound_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(9999L)
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void create_notRunning_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* mock */
        DockerConfig.stopContainer(CONTAINER_1);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void delete_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, InterruptedException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        databaseService.delete(DATABASE_1_ID);
        final Optional<Database> response = databaseRepository.findById(DATABASE_1_ID);
        assertTrue(response.isEmpty());
    }

    @Test
    public void delete_notFound_fails() {

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.delete(9999L);
        });
    }

    @Test
    public void delete_notRunning_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* mock */
        DockerConfig.stopContainer(CONTAINER_1);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    @Disabled("not supported in MariaDB")
    public void modify_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, InterruptedException {
        final DatabaseModifyDto request = DatabaseModifyDto.builder()
                .databaseId(DATABASE_1_ID)
                .isPublic(true)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Database response = databaseService.modify(request);
        assertEquals("DBNAME", response.getName());
        assertTrue(response.getIsPublic());
    }

    @Test
    public void modify_notFound_fails() {

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.delete(CONTAINER_1_ID);
        });
    }

    @Test
    public void modify_notRunning_fails() throws InterruptedException {

        /* mock */
        DockerConfig.stopContainer(CONTAINER_1);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void find_succeeds() throws DatabaseNotFoundException, InterruptedException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Database response = databaseService.findById(DATABASE_1_ID);
        assertEquals(DATABASE_1_ID, response.getId());
        assertEquals(DATABASE_1_NAME, response.getName());
        assertEquals(DATABASE_1_PUBLIC, response.getIsPublic());
    }

    @Test
    public void find_notFound_fails() throws InterruptedException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.findById(CONTAINER_1_ID);
        });
    }

}
