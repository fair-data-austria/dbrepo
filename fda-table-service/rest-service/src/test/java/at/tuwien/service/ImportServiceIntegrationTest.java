package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.ImageRepository;
import at.tuwien.repository.TableRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImportServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private DataService dataService;

    private CreateContainerResponse request1, request2;

    @Transactional
    @BeforeEach
    public void beforeEach() throws InterruptedException {
        afterEach();
        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        imageRepository.save(IMAGE_1);
        /* create container */
        request1 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        request2 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_3_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_3_INTERNALNAME)
                .withIpv4Address(CONTAINER_3_IP)
                .withHostName(CONTAINER_3_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request1.getId()).exec();
        dockerClient.startContainerCmd(request2.getId()).exec();
        Thread.sleep(5 * 1000);
        CONTAINER_1_HASH = request1.getId();
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
        databaseRepository.save(DATABASE_3); /* csv_02 */
    }

    @Transactional
    @AfterEach
    public void afterEach() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    System.out.println("DELETE CONTAINER " + Arrays.toString(container.getNames()));
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
                    System.out.println("DELETE NETWORK " + network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @Test
    public void insertFromFile_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException, TableNotFoundException, FileStorageException {
        tableService.createTable(DATABASE_1_ID, TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build());
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_1, response.get());
    }

    @Test
    public void insertFromFileCsv01_nonUnique_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException, TableNotFoundException, FileStorageException {
        COLUMNS_CSV01[0].setUnique(false);
        tableService.createTable(DATABASE_1_ID, TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build());
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* test */
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_1, response.get());
    }

    @Test
    public void insertFromFileCsv02_succeeds() throws TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException, TableNotFoundException, FileStorageException {
        tableService.createTable(DATABASE_3_ID, TableCreateDto.builder()
                .name(TABLE_3_NAME)
                .description(TABLE_3_DESCRIPTION)
                .columns(COLUMNS_CSV02)
                .build());
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(',')
                .skipHeader(true)
                .nullElement(null)
                .csvLocation("test:src/test/resources/csv/csv_02.csv")
                .build();

        /* test */
        dataService.insertCsv(DATABASE_3_ID, TABLE_3_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_3, TABLE_3_ID);
        assertTrue(response.isPresent());
        assertArrayEquals(COLUMNS_CSV02, response.get().getColumns().toArray(new TableColumn[0]));
    }

    @Test
    public void insertFromFile_columnNumberDiffers_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException, TableMalformedException {
        tableService.createTable(DATABASE_1_ID, TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build());
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_09.csv")
                .build();

        /* test */
        assertThrows(FileStorageException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_2_ID, request);
        });
    }

    @Test
    public void insertFromFile_notRunning_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException, TableMalformedException {
        tableService.createTable(DATABASE_1_ID, TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build());
        dockerClient.stopContainerCmd(CONTAINER_1_HASH).exec();
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("test:src/test/resources/csv/csv_01.csv")
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertCsv(DATABASE_1_ID, TABLE_2_ID, request);
        });
    }

}
