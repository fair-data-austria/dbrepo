package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerStateDto;
import at.tuwien.config.DockerUtil;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DockerUtil dockerUtil;

    /**
     * Unfortunately since we depend on the {@link DockerUtil}, we cannot use it in a static
     * context which dramatically slows down the testing.
     */
    @Transactional
    @BeforeEach
    public void beforeEach() {
        afterEach();
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

        /* create weather container */
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .exec();

        /* set hash */
        CONTAINER_1.setHash(request.getId());

        /* mock data */
        imageRepository.save(IMAGE_1);
        containerRepository.save(CONTAINER_1);
        containerRepository.save(CONTAINER_2);
    }

    @Transactional
    @AfterEach
    public void afterEach() {
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
    public void findIpAddress_succeeds() throws ContainerNotRunningException, InterruptedException {

        /* mock */
        dockerUtil.startContainer(CONTAINER_1);

        /* test */
        final Map<String, String> response = containerService.findIpAddresses(CONTAINER_1.getHash());
        assertTrue(response.containsKey("fda-userdb"));
        assertEquals(CONTAINER_1_IP, response.get("fda-userdb"));
    }

    @Test
    public void findIpAddress_notRunning_fails() {

        /* mock */
        dockerUtil.stopContainer(CONTAINER_1);


        /* test */
        assertThrows(ContainerNotRunningException.class, () -> {
            containerService.findIpAddresses(CONTAINER_1.getHash());
        });
    }

    @Test
    public void findIpAddress_notFound_fails() {

        /* mock */
        dockerUtil.stopContainer(CONTAINER_1);
        dockerUtil.removeContainer(CONTAINER_1);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.findIpAddresses(CONTAINER_1.getHash());
        });
    }

    @Test
    public void getContainerState_succeeds() throws DockerClientException, InterruptedException {

        /* mock */
        dockerUtil.startContainer(CONTAINER_1);

        /* test */
        final ContainerStateDto response = containerService.getContainerState(CONTAINER_1.getHash());
        assertEquals(ContainerStateDto.RUNNING, response);
    }

    @Test
    public void getContainerState_notFound_fails() {

        /* mock */
        dockerUtil.stopContainer(CONTAINER_1);
        dockerUtil.removeContainer(CONTAINER_1);

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.getContainerState(CONTAINER_1.getHash());
        });
    }

    @Test
    public void create_succeeds() throws DockerClientException, ImageNotFoundException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .name(CONTAINER_1_NAME)
                .build();

        /* test */
        final Container container = containerService.create(request);
        assertEquals(CONTAINER_1_NAME, container.getName());
    }


    @Test
    public void findById_notFound_fails() {

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.getById(CONTAINER_3_ID);
        });
    }

    @Test
    public void change_start_succeeds() throws DockerClientException {

        /* mock */
        dockerUtil.stopContainer(CONTAINER_1);

        /* test */
        containerService.start(CONTAINER_1_ID);
    }

    @Test
    public void change_stop_succeeds() throws DockerClientException, InterruptedException {

        /* mock */
        dockerUtil.startContainer(CONTAINER_1);

        /* test */
        containerService.stop(CONTAINER_1_ID);
    }

    @Test
    public void change_stop_notFoundDocker_fails() {

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.stop(CONTAINER_2_ID);
        });
    }

    @Test
    public void remove_succeeds() throws DockerClientException {

        /* mock */
        dockerUtil.stopContainer(CONTAINER_1);

        /* test */
        containerService.remove(CONTAINER_1_ID);
    }

    @Test
    public void remove_notFound_fails() {

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.remove(9999999L);
        });
    }

    @Test
    public void remove_stillRunning_fails() throws InterruptedException {

        /* mock */
        dockerUtil.startContainer(CONTAINER_1);

        /* test */
        assertThrows(ContainerStillRunningException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

}
