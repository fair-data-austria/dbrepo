package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class DataServiceUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DataService dataService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private TableRepository tableRepository;

    @BeforeAll
    public static void beforeAll() throws InterruptedException, SQLException {
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
        final CreateContainerResponse container = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("POSTGRES_USER=postgres", "POSTGRES_PASSWORD=postgres", "POSTGRES_DB=weather")
                .withBinds(Bind.parse(new File("./src/test/resources/weather").toPath().toAbsolutePath()
                        + ":/docker-entrypoint-initdb.d"))
                .exec();
        /* start */
        dockerClient.startContainerCmd(container.getId()).exec();
        Thread.sleep(3000);
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
    public void selectAll_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            DatabaseNotFoundException, ImageNotSupportedException, TableMalformedException {
        final Long page = 0L;
        final Long size = 10L;

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        dataService.selectAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), page, size);
    }

    @Test
    public void selectAll_mariaDb_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            DatabaseNotFoundException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        final Long page = 0L;
        final Long size = 10L;


        /* create container */
        final CreateContainerResponse mariaDbContainer = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_2_INTERNALNAME)
                .withIpv4Address(CONTAINER_2_IP)
                .withHostName(CONTAINER_2_INTERNALNAME)
                .withEnv("MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(new File("./src/test/resources/weather-mariadb").toPath().toAbsolutePath()
                        + ":/docker-entrypoint-initdb.d"))
                .exec();
        dockerClient.startContainerCmd(mariaDbContainer.getId())
                        .exec();
        Thread.sleep(10 * 1000);

        /* mock */
        when(databaseRepository.findById(DATABASE_2_ID))
                .thenReturn(Optional.of(DATABASE_2));
        when(tableRepository.findByDatabaseAndId(DATABASE_2, TABLE_2_ID))
                .thenReturn(Optional.of(TABLE_2));

        /* test */
        dataService.selectAll(DATABASE_2_ID, TABLE_2_ID, Instant.now(), page, size);
    }

    @Test
    public void selectAll_noTable_fails() {
        final Long page = 0L;
        final Long size = 10L;

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            dataService.selectAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), page, size);
        });
    }

    @Test
    public void selectAll_noDatabase_fails() {
        final Long page = 0L;
        final Long size = 10L;

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            dataService.selectAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), page, size);
        });
    }

    @Test
    public void selectAll_parameter_fails() {
        final Long page = -1L;
        final Long size = 10L;

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.selectAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), page, size);
        });
    }

    @Test
    public void selectAll_parameter2_fails() {
        final Long page = 1L;
        final Long size = 0L;

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.selectAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), page, size);
        });
    }

    @Test
    public void insert_columns_fails() {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of("not_existing", "some value")))
                .build();

        /* mock */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insert(TABLE_1, request);
        });
    }

}
