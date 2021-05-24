package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.ContainerNotRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ImageService imageService;

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
    public void beforeEach() {
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
        CONTAINER_1.setHash(request.getId());
        containerRepository.save(CONTAINER_1);
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
        /* entities are deleted automatically by dirties context */
    }

    @Test
    public void findIpAddress_succeeds() throws ContainerNotRunningException {

        /* test */
        final Map<String, String> response = containerService.findIpAddresses(CONTAINER_1.getHash());
        assertTrue(response.containsKey("fda-userdb"));
        assertEquals(CONTAINER_1_IP, response.get("fda-userdb"));
    }

    @Test
    public void findIpAddress_notRunning_fails() {
        dockerClient.startContainerCmd(CONTAINER_1.getHash());

        /* test */
        assertThrows(DockerClientException.class, () -> {
            containerService.findIpAddresses(CONTAINER_1.getHash());
        });
    }

    @Test
    public void findIpAddress_notFound_fails() {

    }

    @Test
    public void getContainerState_succeeds() {

    }

    @Test
    public void getContainerState_notFound_fails() {

    }

    @Test
    public void create_succeeds() {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .build();
    }


    @Test
    public void findById_docker_fails() {
        // cannot test
    }

    @Test
    public void change_start_succeeds() {
        // cannot test
    }

    @Test
    public void change_stop_succeeds() {
        // cannot test
    }

}
