package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JdbcConnector jdbcConnector;

    private static CreateContainerResponse request1, request2;

    @Transactional
    @BeforeEach
    public void beforeEach() throws InterruptedException {
        afterEach();
        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withInternal(true)
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        imageRepository.save(IMAGE_1);
        imageRepository.save(IMAGE_2);
        /* create container */
        request1 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_NAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        request2 = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withEnv(IMAGE_2_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_2_NAME)
                .withIpv4Address(CONTAINER_2_IP)
                .withHostName(CONTAINER_2_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request1.getId()).exec();
        dockerClient.startContainerCmd(request2.getId()).exec();
        Thread.sleep(10 * 1000);
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
    }

    @Transactional
    @AfterEach
    public void afterEach() {
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

        /* test */
        final List<Database> response = databaseService.findAll();
        assertEquals(2, response.size());
    }

    @Test
    public void create_postgres_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, SQLException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
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
    @Disabled("Cannot start in normal time")
    public void create_mariadb_succeeds() throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, InterruptedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_2_ID)
                .name(DATABASE_2_NAME)
                .isPublic(DATABASE_2_PUBLIC)
                .build();
        /* maria db takes ages to boot up */
        Thread.sleep(40 * 1000);

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
        dockerClient.stopContainerCmd(request1.getId()).exec();
        Thread.sleep(3000);
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
            DatabaseMalformedException {

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
        dockerClient.stopContainerCmd(request1.getId()).exec();
        Thread.sleep(3000);
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
            DatabaseMalformedException {
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
        dockerClient.stopContainerCmd(request1.getId()).exec();
        Thread.sleep(3000);

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
