package at.tuwien.mapper;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.PortBinding;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.jooq.impl.DSL.table;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@Log4j2
@ExtendWith(SpringExtension.class)
public class TableMapperIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private Properties postgresProperties;

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @BeforeAll
    public static void beforeAll() {
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
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
        CONTAINER_1.setHash(request.getId());
        /* set database */
        TABLE_1.setDatabase(DATABASE_1);
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

    private DSLContext open() throws SQLException {
        final String jdbc = "jdbc:mariadb://" + CONTAINER_1_IP + "/" + DATABASE_1_INTERNALNAME;
        final Connection connection = DriverManager.getConnection(jdbc, "mariadb", "mariadb");
        return DSL.using(connection, SQLDialect.MARIADB);
    }

    private static TableCreateDto instance() {
        return TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(new ColumnCreateDto[]{
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_1_TYPE_DTO)
                                .name(COLUMN_1_1_NAME)
                                .nullAllowed(COLUMN_1_1_NULL)
                                .primaryKey(COLUMN_1_1_PRIMARY)
                                .unique(COLUMN_1_1_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_2_TYPE_DTO)
                                .name(COLUMN_1_2_NAME)
                                .nullAllowed(COLUMN_1_2_NULL)
                                .primaryKey(COLUMN_1_2_PRIMARY)
                                .unique(COLUMN_1_2_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_3_TYPE_DTO)
                                .name(COLUMN_1_3_NAME)
                                .nullAllowed(COLUMN_1_3_NULL)
                                .primaryKey(COLUMN_1_3_PRIMARY)
                                .unique(COLUMN_1_3_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_4_TYPE_DTO)
                                .name(COLUMN_1_4_NAME)
                                .nullAllowed(COLUMN_1_4_NULL)
                                .primaryKey(COLUMN_1_4_PRIMARY)
                                .unique(COLUMN_1_4_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_5_TYPE_DTO)
                                .name(COLUMN_1_5_NAME)
                                .nullAllowed(COLUMN_1_5_NULL)
                                .primaryKey(COLUMN_1_5_PRIMARY)
                                .unique(COLUMN_1_5_UNIQUE)
                                .build()})
                .build();
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_success() throws SQLException, ArbitraryPrimaryKeysException,
            ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_twoColumnPrimaryKey_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[1]
                .setNullAllowed(false);
        TABLE_2_CREATE_DTO.getColumns()[1]
                .setPrimaryKey(true);
        TABLE_2_CREATE_DTO.getColumns()[1]
                .setType(ColumnTypeDto.NUMBER);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_2_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyBlob_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.BLOB);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyDate_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.DATE);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyText_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.TEXT);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyString_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.STRING);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyEnum_fails() throws SQLException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.ENUM);
        assertThrows(ArbitraryPrimaryKeysException.class, () -> {
            tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                    .execute();
        });
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_noPrimaryKey_succeeds() throws SQLException, InterruptedException,
            TableMalformedException, ArbitraryPrimaryKeysException, ImageNotSupportedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setPrimaryKey(false);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyNull_fails() throws SQLException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setNullAllowed(true);
        assertThrows(ArbitraryPrimaryKeysException.class, () -> {
            tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                    .execute();
        });
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_uniqueConstraint_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_uniqueConstraint2_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[1]
                .setUnique(true);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_1_1_INTERNAL_NAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_1_2_INTERNAL_NAME))
                .count());
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_enum_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        final ColumnCreateDto[] columns = new ColumnCreateDto[]{COLUMNS_CSV01[0], COLUMNS_CSV01[1], COLUMNS_CSV01[2],
                COLUMNS_CSV01[3], COLUMNS_CSV01[4], ColumnCreateDto.builder()
                .name("Gender")
                .nullAllowed(false)
                .primaryKey(false)
                .unique(false)
                .foreignKey(null)
                .type(ColumnTypeDto.ENUM)
                .enumValues(new String[]{"MALE", "FEMALE", "OTHER"})
                .build()};
        TABLE_2_CREATE_DTO.setColumns(columns);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_checkConstraint_success() throws SQLException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_foreignKey_success() throws SQLException, InterruptedException {
        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
    }

}
