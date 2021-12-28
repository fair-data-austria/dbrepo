package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.TableServiceImpl;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TableServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TableServiceImpl tableService;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        afterEach();

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
        CONTAINER_1.setHash(response.getId());

        /* repository */
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_1);
        imageRepository.save(IMAGE_1);
        databaseRepository.save(DATABASE_1);
    }

    @AfterEach
    public void afterEach() {
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
    public void createTable_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
        assertEquals(COLUMNS_CSV01.length, response.getColumns().size());
    }

    @Test
    public void createTable_noPrimaryKeyAutoGenerate_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_2_NAME)
                        .type(COLUMN_1_2_TYPE_DTO)
                        .nullAllowed(COLUMN_1_2_NULL)
                        .unique(COLUMN_1_2_UNIQUE)
                        .primaryKey(false)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_2_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_2_CHECK)
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
        assertEquals(2, response.getColumns().size());
        assertTrue(response.getColumns().get(1).getAutoGenerated());
    }

    @Test
    public void createTable_noPrimaryKeyAutoGenerate_fails() throws InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_1_NAME)
                        .type(COLUMN_1_1_TYPE_DTO)
                        .nullAllowed(COLUMN_1_1_NULL)
                        .unique(COLUMN_1_1_UNIQUE)
                        .primaryKey(false)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_1_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_1_CHECK)
                        .build(),
                ColumnCreateDto.builder()
                        .name(COLUMN_1_2_NAME)
                        .type(COLUMN_1_2_TYPE_DTO)
                        .nullAllowed(COLUMN_1_2_NULL)
                        .unique(COLUMN_1_2_UNIQUE)
                        .primaryKey(false)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_2_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_2_CHECK)
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            tableService.createTable(DATABASE_1_ID, request);
        });
    }

    @Test
    public void createTable_groupPrimaryKey_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_1_NAME)
                        .type(COLUMN_1_1_TYPE_DTO)
                        .nullAllowed(COLUMN_1_1_NULL)
                        .unique(COLUMN_1_1_UNIQUE)
                        .primaryKey(COLUMN_1_1_PRIMARY)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_1_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_1_CHECK)
                        .build(),
                ColumnCreateDto.builder()
                        .name(COLUMN_1_3_NAME)
                        .type(COLUMN_1_3_TYPE_DTO)
                        .nullAllowed(COLUMN_1_3_NULL)
                        .unique(COLUMN_1_3_UNIQUE)
                        .primaryKey(true)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_3_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_3_CHECK)
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();


        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
    }

    @Test
    public void createTable_checkExpression_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_1_NAME)
                        .type(COLUMN_1_1_TYPE_DTO)
                        .nullAllowed(COLUMN_1_1_NULL)
                        .unique(COLUMN_1_1_UNIQUE)
                        .primaryKey(COLUMN_1_1_PRIMARY)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_1_FOREIGN_KEY)
                        .checkExpression("`id` > 0")
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();


        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
    }

    @Test
    public void createTable_withEnum_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_1_NAME)
                        .type(COLUMN_1_1_TYPE_DTO)
                        .nullAllowed(COLUMN_1_1_NULL)
                        .unique(COLUMN_1_1_UNIQUE)
                        .primaryKey(COLUMN_1_1_PRIMARY)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_1_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_1_CHECK)
                        .build(),
                ColumnCreateDto.builder()
                        .name(COLUMN_1_3_NAME)
                        .type(COLUMN_1_3_TYPE_DTO)
                        .nullAllowed(COLUMN_1_3_NULL)
                        .unique(COLUMN_1_3_UNIQUE)
                        .primaryKey(COLUMN_1_3_PRIMARY)
                        .enumValues(new String[]{"A", "B", "C"})
                        .foreignKey(COLUMN_1_3_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_3_CHECK)
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
        assertEquals(2, response.getColumns().size());
        assertEquals(List.of("A","B","C"), response.getColumns().get(1).getEnumValues());
    }

    @Test
    public void createTable_withUniqueColumn_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException {
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{
                ColumnCreateDto.builder()
                        .name(COLUMN_1_1_NAME)
                        .type(COLUMN_1_1_TYPE_DTO)
                        .nullAllowed(COLUMN_1_1_NULL)
                        .unique(COLUMN_1_1_UNIQUE)
                        .primaryKey(COLUMN_1_1_PRIMARY)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_1_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_1_CHECK)
                        .build(),
                ColumnCreateDto.builder()
                        .name(COLUMN_1_3_NAME)
                        .type(COLUMN_1_3_TYPE_DTO)
                        .nullAllowed(COLUMN_1_3_NULL)
                        .unique(true)
                        .primaryKey(COLUMN_1_3_PRIMARY)
                        .enumValues(null)
                        .foreignKey(COLUMN_1_3_FOREIGN_KEY)
                        .checkExpression(COLUMN_1_3_CHECK)
                        .build()
        };
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(columns)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
        assertEquals(2, response.getColumns().size());
    }

    @Test
    public void deleteTable_succeeds() throws DatabaseNotFoundException,
            ImageNotSupportedException, InterruptedException, TableNotFoundException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        tableService.deleteTable(DATABASE_1_ID, TABLE_1_ID);
    }

    /**
     * TODO https://gitlab.phaidra.org/fair-data-austria-db-repository/fda-services/-/issues/99
     * <p>
     * When creating a table (POST /database/1/table) with columns of these types, I get this error:
     * <p>
     * type: "STRING", name: "username"
     * type: "BLOB"
     */
    @Test
    public void createTable_textPrimaryKey_succeeds() throws InterruptedException, SQLException, TableMalformedException,
            ArbitraryPrimaryKeysException, DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name("Issue 99")
                .description("Related to issue 99")
                .columns(new ColumnCreateDto[]{
                        ColumnCreateDto.builder()
                                .name("username")
                                .nullAllowed(false)
                                .type(ColumnTypeDto.TEXT)
                                .unique(true)
                                .primaryKey(true)
                                .build(),
                        ColumnCreateDto.builder()
                                .name("data")
                                .nullAllowed(true)
                                .type(ColumnTypeDto.BLOB)
                                .unique(false)
                                .primaryKey(false)
                                .build()
                })
                .build();

        /* start */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        tableService.createTable(DATABASE_1_ID, request);
    }

    @Test
    public void createTable_blobPrimaryKey_succeeds() throws InterruptedException, SQLException, TableMalformedException,
            ArbitraryPrimaryKeysException, DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name("Issue 99")
                .description("Related to issue 99")
                .columns(new ColumnCreateDto[]{
                        ColumnCreateDto.builder()
                                .name("username")
                                .nullAllowed(false)
                                .type(ColumnTypeDto.BLOB)
                                .unique(true)
                                .primaryKey(true)
                                .build(),
                        ColumnCreateDto.builder()
                                .name("data")
                                .nullAllowed(true)
                                .type(ColumnTypeDto.BLOB)
                                .unique(false)
                                .primaryKey(false)
                                .build()
                })
                .build();

        /* start */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        tableService.createTable(DATABASE_1_ID, request);
    }

    @Test
    public void delete_succeeds() throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException,
            InterruptedException, SQLException {

        /* start */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        tableService.deleteTable(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void createTable_issue106_succeeds() throws InterruptedException, SQLException, TableMalformedException,
            ArbitraryPrimaryKeysException, DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name("Table")
                .description(TABLE_2_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build();

        /* start */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        tableService.createTable(DATABASE_1_ID, request);
    }

}
