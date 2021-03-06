package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.AmqpException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.service.impl.RabbitMqServiceImpl;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
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

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;

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
    private RabbitMqServiceImpl amqpService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private DatabaseRepository databaseRepository;

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
