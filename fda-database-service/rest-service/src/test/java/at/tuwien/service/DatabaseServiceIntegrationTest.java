package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

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

    private CreateContainerResponse response1;

    @BeforeAll
    public static void beforeAll() {
        afterAll();
        final DockerConfig dockerConfig = new DockerConfig();
        final HostConfig hostConfig = dockerConfig.hostConfig();
        final DockerClient dockerClient = dockerConfig.dockerClientConfiguration();
        /* create network */
        final boolean exists = (long) dockerClient.listNetworksCmd()
                .withNameFilter("fda-public", "fda-userdb")
                .exec()
                .size() == 2;
        if (!exists) {
            dockerClient.createNetworkCmd()
                    .withName("fda-userdb")
                    .withInternal(true)
                    .withIpam(new Network.Ipam()
                            .withConfig(new Network.Ipam.Config()
                                    .withSubnet("172.28.0.0/16")))
                    .withEnableIpv6(false)
                    .exec();
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
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
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
                    log.info("Delete Container {}", Arrays.asList(container.getNames()));
                    if (container.getState().equals("running")) {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                });
        /* remove networks */
        dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .filter(n -> n.getName().startsWith("fda"))
                .forEach(network -> {
                    log.info("Delete Network {}", network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @Transactional
    @BeforeEach
    public void beforeEach() {
        imageRepository.save(IMAGE_1);
        imageRepository.save(IMAGE_2);
        databaseRepository.save(DATABASE_1);
        containerRepository.save(CONTAINER_2);
    }

    @AfterEach
    public void afterEach() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    if (Arrays.stream(container.getNames()).noneMatch(c -> c.matches(".*fda-userdb.*"))) {
                        return;
                    }
                    log.info("Delete Container {}", Arrays.asList(container.getNames()));
                    if (container.getState().equals("running")) {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                });
    }

    private void createContainer1(boolean start) throws InterruptedException {
        response1 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_NAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        if (start) {
            dockerClient.startContainerCmd(CONTAINER_1_NAME)
                    .exec();
            Thread.sleep(5 * 1000);
        }
    }

    private void createContainer2(boolean start) throws InterruptedException {
        response1 = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withEnv(IMAGE_2_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_2_NAME)
                .withIpv4Address(CONTAINER_2_IP)
                .withHostName(CONTAINER_2_INTERNALNAME)
                .exec();
        if (start) {
            dockerClient.startContainerCmd(CONTAINER_2_NAME)
                    .exec();
            Thread.sleep(10 * 1000);
        }
    }

    @Test
    public void findAll_succeeds() {

        /* test */
        final List<Database> response = databaseService.findAll();
        assertEquals(1, response.size());
    }

    @Transactional
    @Test
    public void create_postgres_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, SQLException, AmqpException, InterruptedException {
        createContainer1(true);
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID) /* we need this container */
                .name(DATABASE_2_NAME)
                .isPublic(DATABASE_2_PUBLIC)
                .build();

        /* test */
        final Database response = databaseService.create(request);
        assertEquals(DATABASE_2_NAME, response.getName());
        assertEquals(DATABASE_2_PUBLIC, response.getIsPublic());
        assertEquals(CONTAINER_1_ID, response.getContainer().getId());
        final DSLContext context = jdbcConnector.open(response);
        assertTrue(context.meta().getSchemas().size() > 0);
    }

    @Test
    public void create_mariadb_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, InterruptedException, AmqpException {
        createContainer2(true);
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_2_ID)
                .name(DATABASE_2_NAME)
                .isPublic(DATABASE_2_PUBLIC)
                .build();

        /* test */
        final Database response = databaseService.create(request);
        assertEquals(DATABASE_2_NAME, response.getName());
        assertEquals(DATABASE_2_PUBLIC, response.getIsPublic());
        assertEquals(CONTAINER_2_ID, response.getContainer().getId());
    }

    @Test
    public void create_notFound_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(9999L)
                .name(DATABASE_2_NAME)
                .isPublic(DATABASE_2_PUBLIC)
                .build();

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void create_notRunning_fails() throws InterruptedException {
        createContainer1(false);
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void delete_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, InterruptedException {
        createContainer1(true);

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
    public void delete_notRunning_fails() throws InterruptedException {
        createContainer1(false);
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(DATABASE_1_NAME)
                .isPublic(DATABASE_1_PUBLIC)
                .build();

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void modify_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, InterruptedException {
        createContainer1(true);
        final DatabaseModifyDto request = DatabaseModifyDto.builder()
                .databaseId(DATABASE_1_ID)
                .name("DBNAME")
                .isPublic(true)
                .build();

        /* test */
        final Database response = databaseService.modify(request);
        assertEquals("DBNAME", response.getName());
        assertTrue(response.getIsPublic());
    }

    @Test
    public void modify_notFound_fails() {

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.delete(9999L);
        });
    }

    @Test
    public void modify_notRunning_fails() throws InterruptedException {
        createContainer1(false);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void find_succeeds() throws DatabaseNotFoundException {

        /* test */
        final Database response = databaseService.findById(DATABASE_1_ID);
        assertEquals(DATABASE_1_ID, response.getId());
        assertEquals(DATABASE_1_NAME, response.getName());
        assertEquals(DATABASE_1_PUBLIC, response.getIsPublic());
    }

    @Test
    public void find_notFound_fails() {

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.findById(9999L);
        });
    }

}
