package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DataEndpointRestIntegrationTest extends BaseUnitTest {

//    @MockBean
//    private Channel channel;
//
//    @MockBean
//    private ReadyConfig readyConfig;
//
//    @Autowired
//    private ImageRepository imageRepository;
//
//    @Autowired
//    private TableRepository tableRepository;
//
//    @Autowired
//    private DatabaseRepository databaseRepository;
//
//    @Autowired
//    private DataEndpoint dataEndpoint;
//
//    @BeforeAll
//    public static void beforeAll() throws InterruptedException {
//        afterAll();
//        /* create network */
//        dockerClient.createNetworkCmd()
//                .withName("fda-userdb")
//                .withIpam(new Network.Ipam()
//                        .withConfig(new Network.Ipam.Config()
//                                .withSubnet("172.28.0.0/16")))
//                .withEnableIpv6(false)
//                .exec();
//        /* create container */
//        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
//        log.trace("container bind {}", bind);
//        final CreateContainerResponse response = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
//                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
//                .withName(CONTAINER_1_INTERNALNAME)
//                .withIpv4Address(CONTAINER_1_IP)
//                .withHostName(CONTAINER_1_INTERNALNAME)
//                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
//                .withBinds(Bind.parse(bind))
//                .exec();
//        /* set hash */
//        CONTAINER_1.setHash(response.getId());
//    }
//
//    @Transactional
//    @BeforeEach
//    public void beforeEach() {
//        imageRepository.save(IMAGE_1);
//        databaseRepository.save(DATABASE_1);
//        TABLE_1.setDatabase(DATABASE_1);
//        tableRepository.save(TABLE_1);
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        /* stop containers and remove them */
//        dockerClient.listContainersCmd()
//                .withShowAll(true)
//                .exec()
//                .forEach(container -> {
//                    log.info("Delete container {}", Arrays.asList(container.getNames()));
//                    try {
//                        dockerClient.stopContainerCmd(container.getId()).exec();
//                    } catch (NotModifiedException e) {
//                        // ignore
//                    }
//                    dockerClient.removeContainerCmd(container.getId()).exec();
//                });
//        /* remove networks */
//        dockerClient.listNetworksCmd()
//                .exec()
//                .stream()
//                .filter(n -> n.getName().startsWith("fda"))
//                .forEach(network -> {
//                    log.info("Delete network {}", network.getName());
//                    dockerClient.removeNetworkCmd(network.getId()).exec();
//                });
//    }
//
//    @Test
//    public void insertFromTuple_succeeds() throws TableNotFoundException, TableMalformedException,
//            DatabaseNotFoundException, ImageNotSupportedException, SQLException, InterruptedException {
//        final Map<String, Object> map = new LinkedHashMap<>() {{
//            put(COLUMN_1_1_NAME, 4);
//            put(COLUMN_1_2_NAME, "2020-11-01");
//            put(COLUMN_1_3_NAME, "Sydney");
//            put(COLUMN_1_4_NAME, 35.2);
//            put(COLUMN_1_5_NAME, 10.2);
//        }};
//        final TableCsvDto request = TableCsvDto.builder()
//                .data(List.of(map))
//                .build();
//
//        /* mock */
//        DockerConfig.startContainer(CONTAINER_1);
//        MariaDbConfig.clearDatabase(TABLE_1);
//
//        /* test */
//        final ResponseEntity<?> response = dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
//        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
//    }
//
//    @Test
//    public void insertFromTuple_empty_succeeds() throws SQLException, TableNotFoundException, TableMalformedException,
//            DatabaseNotFoundException, ImageNotSupportedException {
//        final TableCsvDto request = TableCsvDto.builder()
//                .data(List.of())
//                .build();
//
//        /* mock */
//        MariaDbConfig.clearDatabase(TABLE_1);
//
//        /* test */
//        dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
//    }
//
//    @Test
//    public void insertFromTuple_empty2_succeeds() throws SQLException, InterruptedException, TableNotFoundException,
//            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException {
//        final TableCsvDto request = TableCsvDto.builder()
//                .data(List.of(Map.of()))
//                .build();
//
//        /* mock */
//        DockerConfig.startContainer(CONTAINER_1);
//        MariaDbConfig.clearDatabase(TABLE_1);
//
//        /* test */
//        dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
//    }
//
//    @Test
//    public void insertFromFile_succeeds() throws TableNotFoundException, TableMalformedException,
//            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, SQLException,
//            InterruptedException {
//        final String request = "test:csv/csv_01.csv";
//
//        /* mock */
//        DockerConfig.startContainer(CONTAINER_1);
//        MariaDbConfig.clearDatabase(TABLE_1);
//
//        /* test */
//        final ResponseEntity<?> response = dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request);
//        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
//    }
//
//    @Test
//    public void getAll_succeeds() throws TableNotFoundException, TableMalformedException,
//            DatabaseNotFoundException, ImageNotSupportedException, SQLException, DatabaseConnectionException,
//            InterruptedException, PaginationException {
//        final Instant timestamp = Instant.now();
//        final Long page = 0L;
//        final Long size = 1L;
//
//        /* mock */
//        DockerConfig.startContainer(CONTAINER_1);
//        MariaDbConfig.clearDatabase(TABLE_1);
//
//        /* test */
//        final ResponseEntity<?> response = dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, timestamp, page, size);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

}
