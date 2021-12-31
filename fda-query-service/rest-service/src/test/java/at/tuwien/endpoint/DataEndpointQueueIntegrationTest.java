package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class DataEndpointQueueIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DataEndpoint dataEndpoint;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * We need a container to test the CRUD operations as of now it is unfeasible to determine the correctness of the
     * operations without a live container
     *
     * @throws InterruptedException Sleep interrupted.
     */
    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        afterAll();
        /* create networks */
        dockerClient.createNetworkCmd()
                .withName(BROKER_NET)
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.29.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        dockerClient.createNetworkCmd()
                .withName(DATABASE_NET)
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        /* create broker container */
        final CreateContainerResponse response1 = dockerClient.createContainerCmd(BROKER_IMAGE)
                .withHostConfig(hostConfig.withNetworkMode(BROKER_NET))
                .withName(BROKER_INTERNALNAME)
                .withIpv4Address(BROKER_IP)
                .withHostName(BROKER_INTERNALNAME)
                .withEnv("TZ=Europe/Vienna")
                .exec();
        dockerClient.startContainerCmd(response1.getId())
                .exec();
        /* create table container */
        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind);
        final CreateContainerResponse response2 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode(DATABASE_NET))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
        /* wait */
        CONTAINER_1.setHash(response2.getId());
        Thread.sleep(10 * 1000);
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
    @Transactional
    public void beforeEach() {
        imageRepository.save(IMAGE_1);
        TABLE_1.setDatabase(DATABASE_1);
        tableRepository.save(TABLE_1);
    }

    @Test
    public void insertFromTuple_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, InterruptedException, SQLException {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(new LinkedHashMap<>() {{
                    put(COLUMN_1_1_NAME, 1L);
                    put(COLUMN_1_2_NAME, "2020-12-01");
                    put(COLUMN_1_3_NAME, "Somewhere");
                    put(COLUMN_1_4_NAME, 15.0);
                    put(COLUMN_1_5_NAME, 20.0);
                }}))
                .build();

        /*  mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
        assertTrue(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, 1L));
        assertFalse(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, 2L));
    }

    @Test
    public void insertFromTuple_10k_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, InterruptedException, SQLException {
        /* config */
        final long limit = 10_000L;

        /* mock */
        final Random random = new Random();
        final List<Map<String, Object>> data = new LinkedList<>();
        for (long i = 0L; i <= limit; i++) {
            final Long id = i;
            data.add(new LinkedHashMap<>() {{
                put(COLUMN_1_1_NAME, id);
                put(COLUMN_1_2_NAME, "2020-01-01");
                put(COLUMN_1_3_NAME, NanoIdUtils.randomNanoId());
                put(COLUMN_1_4_NAME, random.nextDouble());
                put(COLUMN_1_5_NAME, random.nextDouble());
            }});
        }
        final TableCsvDto request = TableCsvDto.builder()
                .data(data)
                .build();
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final long start = System.currentTimeMillis();
        dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
        final long end = System.currentTimeMillis();
        log.info("Inserted {}k records in {} seconds", limit / 1000, (end - start) / 1000.0);
        assertTrue(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, 1L), "id 1 missing");
        assertTrue(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, 1_000L), "id 1k missing");
        assertTrue(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, 10_000L), "id 10k missing");
        assertTrue(MariaDbConfig.contains(TABLE_1, COLUMN_1_1_NAME, limit), "id 100k missing");
    }

}
