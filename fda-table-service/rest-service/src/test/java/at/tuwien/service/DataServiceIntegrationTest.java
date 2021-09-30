package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.config.PostgresConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.MariaDataService;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private MariaDataService dataService;

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
        /* set hash */
        CONTAINER_1.setHash(request.getId());
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

    @Transactional
    @BeforeEach
    public void beforeEach() {
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
    }

    @Test
    public void insertFromFile_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException, FileStorageException,
            SQLException, InterruptedException {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* mock */
        startContainer();
        PostgresConfig.clearDatabase();

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_1, response.get());
    }

    @Test
    public void insertFromFileCsv01_nonUnique_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException, FileStorageException,
            SQLException, InterruptedException {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* mock */
        startContainer();
        PostgresConfig.clearDatabase();

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_1, response.get());
    }

    @Test
    public void insertFromFileCsv01_wrongDelimiter_fails() throws SQLException, InterruptedException {
        COLUMNS_CSV01[0].setUnique(false);
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* mock */
        startContainer();
        PostgresConfig.clearDatabase();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromFileCsv02_wrongColumnOrder_fails() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException, FileStorageException,
            SQLException, InterruptedException {
        COLUMNS_CSV01[0].setUnique(false);
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_02.csv")
                .build();

        /* mock */
        startContainer();
        PostgresConfig.clearDatabase();

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_1, response.get());
    }

    @Test
    public void insertFromFile_columnNumberDiffers_fails() throws SQLException, InterruptedException {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_09.csv")
                .build();

        /* mock */
        startContainer();
        PostgresConfig.clearDatabase();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromFile_notRunning_fails() {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* mock */
        stopContainer();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    private static void startContainer() throws InterruptedException {
        final InspectContainerResponse inspect = dockerClient.inspectContainerCmd(CONTAINER_1.getHash())
                .exec();
        if (Objects.equals(inspect.getState().getStatus(), "running")) {
            return;
        }
        dockerClient.startContainerCmd(CONTAINER_1.getHash())
                .exec();
        Thread.sleep(3000L);
    }

    private static void stopContainer() {
        dockerClient.stopContainerCmd(CONTAINER_1.getHash())
                .exec();
    }

}
