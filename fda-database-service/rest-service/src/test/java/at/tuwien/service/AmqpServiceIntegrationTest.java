package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.AmqpException;
import at.tuwien.repository.jpa.DatabaseRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AmqpServiceIntegrationTest extends BaseUnitTest {

    private static final String AMQP_EXCHANGE = "fda";

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private Channel channel;

    @Autowired
    private AmqpService amqpService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private DatabaseRepository databaseRepository;

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
        dockerClient.startContainerCmd(request.getId())
                .exec();
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

    @Test
    public void createExchange_succeeds() throws AmqpException, IOException {
        amqpService.createExchange(DATABASE_1);

        channel.queueDeclare(TABLE_1_TOPIC, false, false, false, null);
        channel.queueBind(TABLE_1_TOPIC, DATABASE_1_EXCHANGE, TABLE_1_TOPIC);

        /* test */
        rabbitTemplate.convertAndSend(DATABASE_1_EXCHANGE, TABLE_1_TOPIC, "message");
    }

    @Test
    public void deleteExchange_succeeds() throws AmqpException {

        /* test */
        amqpService.deleteExchange(DATABASE_1);
    }

}
