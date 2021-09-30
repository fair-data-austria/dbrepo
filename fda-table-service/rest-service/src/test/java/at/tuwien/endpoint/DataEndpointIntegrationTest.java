package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.*;
import at.tuwien.config.PostgresConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.DataEndpoint;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.MariaDataService;
import at.tuwien.service.impl.TableServiceImpl;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DataEndpointIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private DataEndpoint dataEndpoint;

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
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("POSTGRES_USER=postgres", "POSTGRES_PASSWORD=postgres", "POSTGRES_DB=weather")
                .withBinds(Bind.parse(new File("./src/test/resources/weather").toPath().toAbsolutePath()
                        + ":/docker-entrypoint-initdb.d"))
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
        Thread.sleep(3000);
    }

    @Transactional
    @BeforeEach
    public void beforeEach() {
        imageRepository.save(IMAGE_1);
        databaseRepository.save(DATABASE_1);
        tableRepository.save(TABLE_1);
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
    public void insertFromTuple_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, SQLException {
        final Map<String, Object> map = new LinkedHashMap<>() {{
            put(COLUMN_1_NAME, 4);
            put(COLUMN_2_NAME, Instant.now());
            put(COLUMN_3_NAME, 35.2);
            put(COLUMN_4_NAME, "Sydney");
            put(COLUMN_5_NAME, 10.2);
        }};
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(map))
                .build();

        /* mock */
        PostgresConfig.clearDatabase();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void insertFromTuple_empty_fails() throws SQLException {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of())
                .build();

        /* mock */
        PostgresConfig.clearDatabase();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromTuple_empty2_fails() throws SQLException {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of()))
                .build();

        /* mock */
        PostgresConfig.clearDatabase();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromFile_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, SQLException {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* mock */
        PostgresConfig.clearDatabase();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.insertFromFile(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void getAll_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, SQLException, DatabaseConnectionException {
        final Instant timestamp = Instant.now();
        final Long page = 0L;
        final Long size = 1L;

        /* mock */
        PostgresConfig.clearDatabase();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, timestamp, page, size);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
