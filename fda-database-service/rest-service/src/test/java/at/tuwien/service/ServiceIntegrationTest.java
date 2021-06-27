package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.PortBinding;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled
public class ServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ImageMapper imageMapper;

    private Long CONTAINER_1_ID, CONTAINER_2_ID;

    @Transactional
    @BeforeEach
    public void beforeEach() {
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
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENV)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_NAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
        CONTAINER_1_HASH = request.getId();
        CONTAINER_1.setHash(CONTAINER_1_HASH);
        CONTAINER_1_ID = containerRepository.save(CONTAINER_1).getId();
        CONTAINER_2_ID = containerRepository.save(CONTAINER_2).getId();
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
                    dockerClient.removeContainerCmd(container.getId()).exec();
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
    public void findAll_empty_succeeds() {

        /* test */
        final List<Database> response = databaseService.findAll();
        assertEquals(0, response.size());
    }

    @Test
    public void create_succeeds() throws SQLException, ImageNotSupportedException, ContainerNotFoundException {
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
    }

    @Test
    public void create_notFound_fails() {

    }

    @Test
    public void create_notRunning_fails() {

    }

    @Test
    public void delete_succeeds() {

    }

    @Test
    public void delete_notFound_fails() {

    }

    @Test
    public void delete_notRunning_fails() {

    }

    @Test
    public void modify_succeeds() {

    }

    @Test
    public void modify_notFound_fails() {

    }

    @Test
    public void modify_notRunning_fails() {

    }

    @Test
    public void find_succeeds() {

    }

    @Test
    public void find_notFound_fails() {

    }

}
