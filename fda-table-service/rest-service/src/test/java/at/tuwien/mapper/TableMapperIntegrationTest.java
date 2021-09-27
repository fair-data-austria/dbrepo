package at.tuwien.mapper;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

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
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private Properties postgresProperties;

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    private String CONTAINER_1_IP;

    @Transactional
    @BeforeEach
    public void beforeEach() throws InterruptedException {
        afterEach();
        /* create container */
        final CreateContainerResponse request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("bridge"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withPortBindings(PortBinding.parse("5433:5432"))
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request.getId())
                .exec();
        Thread.sleep(3000);
        CONTAINER_1_IP = dockerClient.inspectContainerCmd(request.getId())
                .exec()
                .getNetworkSettings()
                .getNetworks()
                .get("bridge")
                .getIpAddress();
    }

    @AfterEach
    public void afterEach() {
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
    }

    private DSLContext open() throws SQLException {
        final String url = "jdbc:postgresql://" + CONTAINER_1_IP + "/" + DATABASE_1_INTERNALNAME;
        final Connection connection = DriverManager.getConnection(url, postgresProperties);
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    private static TableCreateDto instance() {
        return TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(new ColumnCreateDto[]{
                        ColumnCreateDto.builder()
                                .type(COLUMN_1_TYPE_DTO)
                                .name(COLUMN_1_NAME)
                                .nullAllowed(COLUMN_1_NULL)
                                .primaryKey(COLUMN_1_PRIMARY)
                                .unique(COLUMN_1_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_2_TYPE_DTO)
                                .name(COLUMN_2_NAME)
                                .nullAllowed(COLUMN_2_NULL)
                                .primaryKey(COLUMN_2_PRIMARY)
                                .unique(COLUMN_2_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_3_TYPE_DTO)
                                .name(COLUMN_3_NAME)
                                .nullAllowed(COLUMN_3_NULL)
                                .primaryKey(COLUMN_3_PRIMARY)
                                .unique(COLUMN_3_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_4_TYPE_DTO)
                                .name(COLUMN_4_NAME)
                                .nullAllowed(COLUMN_4_NULL)
                                .primaryKey(COLUMN_4_PRIMARY)
                                .unique(COLUMN_4_UNIQUE)
                                .build(),
                        ColumnCreateDto.builder()
                                .type(COLUMN_5_TYPE_DTO)
                                .name(COLUMN_5_NAME)
                                .nullAllowed(COLUMN_5_NULL)
                                .primaryKey(COLUMN_5_PRIMARY)
                                .unique(COLUMN_5_UNIQUE)
                                .build()})
                .build();
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_success() throws SQLException, ArbitraryPrimaryKeysException,
            ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();

        /* test */
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
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
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

        /* test */
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
                .map(f -> f.getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getPrimaryKey)
                .filter(Objects::nonNull)
                .map(Key::getFields)
                .map(k -> k.get(0))
                .map(f -> f.getName().matches(COLUMN_2_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyBlob_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.BLOB);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();

        /* test */
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
                .map(f -> f.getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyDate_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.DATE);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();

        /* test */
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
                .map(f -> f.getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyText_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.TEXT);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();

        /* test */
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
                .map(f -> f.getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyString_succeeds() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.STRING);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();

        /* test */
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
                .map(f -> f.getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyEnum_fails() throws SQLException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setType(ColumnTypeDto.ENUM);

        /* test */
        assertThrows(ArbitraryPrimaryKeysException.class, () -> {
            tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                    .execute();
        });
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_noPrimaryKey_fails() throws SQLException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setPrimaryKey(false);

        /* test */
        assertThrows(ArbitraryPrimaryKeysException.class, () -> {
            tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                    .execute();
        });
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_primaryKeyNull_fails() throws SQLException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[0]
                .setNullAllowed(true);

        /* test */
        assertThrows(ArbitraryPrimaryKeysException.class, () -> {
            tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                    .execute();
        });
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_uniqueConstraint_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();

        /* test */
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_uniqueConstraint2_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();
        TABLE_2_CREATE_DTO.getColumns()[1]
                .setUnique(true);

        /* test */
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_1_INTERNAL_NAME))
                .count());
        assertEquals(1, context.meta()
                .getTables()
                .stream()
                .filter(t -> t.getName().matches(TABLE_2_INTERNALNAME))
                .map(Table::getKeys)
                .map(uniqueKeys -> uniqueKeys.get(0))
                .map(uniqueKey -> uniqueKey.constraint().getName().matches(COLUMN_2_INTERNAL_NAME))
                .count());
    }

    @Test
    public void tableCreateDtoToCreateTableColumnStep_enum_success() throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
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

        /* test */
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, TABLE_2_CREATE_DTO)
                .execute();
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_checkConstraint_success() throws SQLException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();

        /* test */
    }

    @Test
    @Disabled
    public void tableCreateDtoToCreateTableColumnStep_foreignKey_success() throws SQLException {
        final DSLContext context = open();
        final TableCreateDto TABLE_2_CREATE_DTO = instance();

        /* test */
    }

}
