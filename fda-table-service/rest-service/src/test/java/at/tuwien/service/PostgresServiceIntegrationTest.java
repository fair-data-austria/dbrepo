package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.mapper.TableMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.netflix.discovery.converters.Auto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostgresServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private PostgresService postgresService;

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private Properties postgresProperties;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        afterEach();
        /* create container */
        final CreateContainerResponse request = dockerClient.createContainerCmd("postgres:latest")
                .withEnv(IMAGE_1_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("bridge")
                        .withPortBindings(PortBinding.parse("5433:5432")))
                .withName(CONTAINER_1_INTERNALNAME)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        System.out.println("CREATE CONTAINER " + CONTAINER_1_INTERNALNAME);
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
        System.out.println("START CONTAINER " + CONTAINER_1_INTERNALNAME);
        CONTAINER_1_HASH = request.getId();
        CONTAINER_1.setHash(CONTAINER_1_HASH);
        System.out.println("Wait 5s for DB to get up");
        Thread.sleep(5 * 1000);
    }

    @AfterEach
    public void afterEach() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    try {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                        System.out.println("STOP CONTAINER " + Arrays.toString(container.getNames()));
                    } catch (NotModifiedException e) {
                        // ignore
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                    System.out.println("DELETE CONTAINER " + Arrays.toString(container.getNames()));
                });
    }

    @Test
    public void createTable_succeeds() throws DatabaseConnectionException, TableMalformedException, DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_NAME)
                .columns(COLUMNS5)
                .build();

        /* test */
        postgresService.createTable(DATABASE_1, request);
    }

    @Test
    public void createTable_noConnection_fails() {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_NAME)
                .columns(COLUMNS5)
                .build();

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            postgresService.createTable(DATABASE_2, request);
        });
    }

    @Disabled("cannot test")
    @Test
    public void createTable_noSql_fails() throws DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_NAME)
                .columns(COLUMNS5)
                .build();
        final PostgresService mockService = mock(PostgresService.class);

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            postgresService.createTable(DATABASE_1, request);
        });
    }

    @Disabled("not testable for me")
    @Test
    public void insertIntoTable_succeeds() throws SQLException, DatabaseConnectionException, DataProcessingException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://" + CONTAINER_1_INTERNALNAME + ":" + IMAGE_1_PORT + "/" + DATABASE_1_INTERNALNAME, postgresProperties);
        connection.prepareStatement(COLUMNS5_CREATE).execute();
        final List<Map<String, Object>> data = List.of(Map.of(COLUMN_1_NAME, 1, COLUMN_2_NAME, 2, COLUMN_3_NAME, 3, COLUMN_4_NAME, 4, COLUMN_5_NAME, "Description"));
        final List<String> headers = List.of(COLUMN_1_NAME, COLUMN_2_NAME, COLUMN_3_NAME, COLUMN_4_NAME, COLUMN_5_NAME);

        postgresService.insertIntoTable(DATABASE_1, TABLE_1, data, headers);
    }

    @Disabled("not testable for me")
    @Test
    public void insertIntoTable_noConnection_fails() {

    }

    @Disabled("not testable for me")
    @Test
    public void insertIntoTable_noSql_fails() {

    }

    @Disabled("not testable for me")
    @Test
    public void getAllRows_succeeds() {

    }

    @Disabled("not testable for me")
    @Test
    public void getAllRows_noSql_fails() {

    }

    @Disabled("not testable for me")
    @Test
    public void getCreateTableStatement_noSql_fails() {

    }

    @Disabled("not testable for me")
    @Test
    public void insertStatement_succeeds() {

    }

    @Disabled("not testable for me")
    @Test
    public void getDeleteStatement_noSql_fails() {

    }

}
