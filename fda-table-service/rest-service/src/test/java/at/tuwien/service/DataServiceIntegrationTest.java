package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.PostgresConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
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
import java.util.List;
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
    private ImageRepository imageRepository;

    @Autowired
    private MariaDataService dataService;

    @BeforeAll
    public static void beforeAll() {
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
        /* create weather container */
        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind);
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
        /* create file container */
        final String bind2 = new File("./src/test/resources/csv").toPath().toAbsolutePath() + ":/usr/share/nginx/html:ro";
        log.trace("container bind2 {}", bind2);
        final CreateContainerResponse request2 = dockerClient.createContainerCmd(CONTAINER_NGINX_IMAGE + ":" + CONTAINER_NGINX_TAG)
                .withHostConfig(hostConfig.withNetworkMode(CONTAINER_NGINX_NET))
                .withName(CONTAINER_NGINX_NAME)
                .withIpv4Address(CONTAINER_NGINX_IP)
                .withHostName(CONTAINER_NGINX_INTERNALNAME)
                .withBinds(Bind.parse(bind2))
                .exec();
        /* set hash */
        CONTAINER_1.setHash(request.getId());
        CONTAINER_NGINX.setHash(request2.getId());
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
        imageRepository.save(IMAGE_1);
        imageRepository.save(IMAGE_2);
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
        TABLE_3.setDatabase(DATABASE_3);
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
        databaseRepository.save(DATABASE_3);
    }

    @Test
    public void insertFromFile_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException, FileStorageException,
            SQLException, InterruptedException {
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:csv/csv_01.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

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
                .csvLocation("test:csv/csv_01.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

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
                .csvLocation("test:csv/csv_01.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

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
                .csvLocation("test:csv/csv_02.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

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
                .csvLocation("test:csv/csv_09.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

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
                .csvLocation("test:csv/csv_01.csv")
                .build();

        /* mock */
        DockerConfig.stopContainer(CONTAINER_1);

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromRemote_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, InterruptedException,
            SQLException {
        final TableInsertDto request = TableInsertDto.builder()
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("http://172.29.0.3/csv_01.csv")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        DockerConfig.startContainer(CONTAINER_NGINX);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
    }

}
