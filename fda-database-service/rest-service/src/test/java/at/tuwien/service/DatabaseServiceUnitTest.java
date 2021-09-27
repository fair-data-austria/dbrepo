package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.channels.Channel;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DatabaseServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private Channel channel;

    @Autowired
    private DatabaseService databaseService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private ContainerRepository containerRepository;

    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        final DockerConfig dockerConfig = new DockerConfig();
        final HostConfig hostConfig = dockerConfig.hostConfig();
        final DockerClient dockerClient = dockerConfig.dockerClientConfiguration();
        /* create network */final boolean exists = (long) dockerClient.listNetworksCmd()
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
        dockerClient.startContainerCmd(request.getId())
                .exec();
        Thread.sleep(5 * 1000);
    }

    @AfterAll
    public static void afterAll() {
        final DockerConfig dockerConfig = new DockerConfig();
        final DockerClient dockerClient = dockerConfig.dockerClientConfiguration();
        /* stop containers and remove them */
        dockerClient.stopContainerCmd(BROKER_NAME)
                .exec();
        dockerClient.removeContainerCmd(BROKER_NAME)
                .exec();
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

    @Test
    public void findAll_succeeds() {
        when(databaseRepository.findAll())
                .thenReturn(List.of(DATABASE_1));

        final List<Database> response = databaseService.findAll();

        /* test */
        assertEquals(1, response.size());
        assertEquals(DATABASE_1, response.get(0));
    }

    @Test
    public void findById_succeeds() throws DatabaseNotFoundException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));

        final Database response = databaseService.findById(DATABASE_1_ID);

        /* test */
        assertEquals(DATABASE_1, response);
    }

    @Test
    public void findById_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.findById(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void create_notFound_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .containerId(CONTAINER_1_ID)
                .build();
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void modify_notFound_fails() {
        final DatabaseModifyDto request = DatabaseModifyDto.builder()
                .databaseId(DATABASE_1_ID)
                .name("NAME")
                .isPublic(true)
                .build();
        when(databaseRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.modify(request);
        });
    }

}
