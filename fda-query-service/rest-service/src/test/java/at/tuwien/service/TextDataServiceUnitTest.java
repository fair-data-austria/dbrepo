package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.repository.jpa.DatabaseRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class TextDataServiceUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DataService dataService;

    @Autowired
    private TextDataService textDataService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private TableRepository tableRepository;

    /**
     * We need a container to test the CRUD operations as of now it is unfeasible to determine the correctness of the
     * operations without a live container
     *
     * @throws InterruptedException Sleep interrupted.
     */
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
        /* start */
        CONTAINER_1.setHash(response.getId());
        DockerConfig.startContainer(CONTAINER_1);
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
    }

    @Test
    public void read_succeeds() throws IOException, CsvException, TableNotFoundException, DatabaseNotFoundException {
        final String location = "test:csv/csv_01.csv";
        final Character separator = ',';
        final Boolean skipHeader = true;
        final String nullElement = null;
        final String trueElement = "1";
        final String falseElement = "0";

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final TableCsvDto response = textDataService.read(DATABASE_1_ID, TABLE_1_ID, location, separator, skipHeader,
                nullElement, trueElement, falseElement);
        assertEquals(1000, response.getData().size());
    }

    @Test
    public void read_nullElement_succeeds() throws IOException, CsvException, TableNotFoundException,
            DatabaseNotFoundException {
        final String location = "test:csv/csv_01.csv";
        final Character separator = ',';
        final Boolean skipHeader = true;
        final String nullElement = "NA";
        final String trueElement = "1";
        final String falseElement = "0";

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final TableCsvDto response = textDataService.read(DATABASE_1_ID, TABLE_1_ID, location, separator, skipHeader,
                nullElement, trueElement, falseElement);
        assertEquals(1000, response.getData().size());
    }

    @Test
    public void read_skipHeader_succeeds() throws IOException, CsvException, TableNotFoundException,
            DatabaseNotFoundException {
        final String location = "test:csv/csv_01.csv";
        final Character separator = ',';
        final Boolean skipHeader = false;
        final String nullElement = null;
        final String trueElement = "1";
        final String falseElement = "0";

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final TableCsvDto response = textDataService.read(DATABASE_1_ID, TABLE_1_ID, location, separator, skipHeader,
                nullElement, trueElement, falseElement);
        assertEquals(1001, response.getData().size());
    }

    @Test
    public void read_skipHeaderYes_succeeds() throws IOException, CsvException, TableNotFoundException,
            DatabaseNotFoundException {
        final String location = "test:csv/csv_01.csv";
        final Character separator = ',';
        final Boolean skipHeader = false;
        final String nullElement = null;
        final String trueElement = "1";
        final String falseElement = "0";

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final TableCsvDto response = textDataService.read(DATABASE_1_ID, TABLE_1_ID, location, separator, skipHeader,
                nullElement, trueElement, falseElement);
        assertEquals(1001, response.getData().size());
    }

}
