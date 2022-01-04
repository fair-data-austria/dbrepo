package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.repository.elastic.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.opencsv.exceptions.CsvException;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CsvServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DataService dataService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        afterAll();
        /* create network */
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
        /* create container */
        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind);
        final CreateContainerResponse response = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
        final String bind2 = new File("./src/test/resources/webserver").toPath().toAbsolutePath() + ":/usr/share/nginx/html:ro";
        log.trace("container bind {}", bind2);
        final CreateContainerResponse response2 = dockerClient.createContainerCmd(CONTAINER_NGINX_IMAGE + ":" + CONTAINER_NGINX_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(CONTAINER_NGINX_NAME)
                .withIpv4Address(CONTAINER_NGINX_IP)
                .withHostName(CONTAINER_NGINX_INTERNALNAME)
                .withBinds(Bind.parse(bind2))
                .exec();
        /* start */
        CONTAINER_1.setHash(response.getId());
        CONTAINER_2.setHash(response2.getId());
        DockerConfig.startContainer(CONTAINER_1);
        DockerConfig.startContainer(CONTAINER_2);
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

    @BeforeEach
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
        tableRepository.save(TABLE_1);
        tableRepository.save(TABLE_2);
    }

    @Test
    public void write_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, PaginationException {

        /* test */
        final Resource response = dataService.write(DATABASE_1_ID, TABLE_1_ID);
        assertTrue(response.exists());
    }

    @Test
    public void read_url_succeeds() throws IOException, CsvException, TableNotFoundException, DatabaseNotFoundException {
        final String location = "http://" + CONTAINER_NGINX_IP + "/weather_aus.csv";

        /* test */
        final TableCsvDto response = dataService.read(DATABASE_1_ID, TABLE_1_ID, location);
        assertEquals(3, response.getData().size());
    }

}
