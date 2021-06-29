package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerStateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private ContainerService containerService;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DockerClient dockerClient;

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
        /* create container */
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb")
                        .withPortBindings(PortBinding.parse("5433:" + IMAGE_1_PORT)))
                .withName(CONTAINER_1_NAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
        Thread.sleep(3000L);
        CONTAINER_1.setHash(request.getId());
        containerRepository.save(CONTAINER_1).getId();
        containerRepository.save(CONTAINER_2).getId();
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
    public void findIpAddress_succeeds() throws ContainerNotRunningException {

        /* test */
        final Map<String, String> response = containerService.findIpAddresses(CONTAINER_1_HASH);
        assertTrue(response.containsKey("fda-userdb"));
        assertEquals(CONTAINER_1_IP, response.get("fda-userdb"));
    }

    @Test
    public void findIpAddress_notRunning_fails() {
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();

        /* test */
        assertThrows(ContainerNotRunningException.class, () -> {
            containerService.findIpAddresses(CONTAINER_1_HASH);
        });
    }

    @Test
    public void findIpAddress_notFound_fails() {
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();
        dockerClient.removeContainerCmd(CONTAINER_1_HASH).exec();

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.findIpAddresses(CONTAINER_1_HASH);
        });
    }

    @Test
    public void getContainerState_succeeds() throws DockerClientException {

        /* test */
        final ContainerStateDto response = containerService.getContainerState(CONTAINER_1_HASH);
        assertEquals(ContainerStateDto.RUNNING, response);
    }

    @Test
    public void getContainerState_notFound_fails() {
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();
        dockerClient.removeContainerCmd(CONTAINER_1_HASH).exec();

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.getContainerState(CONTAINER_1_HASH);
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
    public void findById_docker_fails() {

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            containerService.getById(9999999L);
        });
    }

    @Test
    public void change_start_succeeds() throws DockerClientException {
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();

        /* test */
        containerService.start(CONTAINER_1_ID);
    }

    @Test
    public void change_stop_succeeds() throws DockerClientException {

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
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();

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
    public void remove_docker_fails() {

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.remove(CONTAINER_2_ID);
        });
    }

    @Test
    public void remove_stillRunning_fails() {

        /* test */
        assertThrows(ContainerStillRunningException.class, () -> {
            containerService.remove(CONTAINER_1_ID);
        });
    }

}
