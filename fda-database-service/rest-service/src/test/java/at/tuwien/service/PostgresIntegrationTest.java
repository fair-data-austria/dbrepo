package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseMalformedException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * We cannot use the postgreSQL service directly since it connects within the docker network, we need "127.0.0.1" = localhost
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional // rollback on each test
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostgresIntegrationTest extends BaseUnitTest {

    @MockBean
    private PostgresService postgresService;

    @Autowired
    private Properties postgresProperties;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @BeforeEach
    public void beforeEach() throws SQLException, InterruptedException {
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
    public void create_succeeds() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenReturn(connection);
        when(postgresService.getCreateDatabaseStatement(connection, DATABASE_2))
                .thenCallRealMethod();
        doCallRealMethod()
                .when(postgresService)
                .create(DATABASE_2);
        /* test */
        postgresService.create(DATABASE_2);
    }

    @Test
    public void create_noConnection_fails() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenThrow(SQLException.class);
        when(postgresService.getCreateDatabaseStatement(connection, DATABASE_1))
                .thenCallRealMethod();
        doCallRealMethod()
                .when(postgresService)
                .create(DATABASE_1);

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            postgresService.create(DATABASE_1);
        });
    }

    @Test
    public void create_invalidSyntax_fails() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenReturn(connection);
        when(postgresService.getCreateDatabaseStatement(connection, DATABASE_1))
                .thenThrow(SQLException.class);
        doCallRealMethod()
                .when(postgresService)
                .create(DATABASE_1);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            postgresService.create(DATABASE_1);
        });
    }

    @Test
    public void delete_succeeds() throws DatabaseConnectionException, DatabaseMalformedException {
        postgresService.delete(DATABASE_1);
    }

    @Test
    public void delete_noConnection_fails() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenThrow(SQLException.class);
        when(postgresService.getDeleteDatabaseStatement(connection, DATABASE_1))
                .thenCallRealMethod();
        doCallRealMethod()
                .when(postgresService)
                .delete(DATABASE_1);

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            postgresService.delete(DATABASE_1);
        });
    }

    @Test
    public void delete_invalidSyntax_fails() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenReturn(connection);
        when(postgresService.getDeleteDatabaseStatement(connection, DATABASE_1))
                .thenThrow(SQLException.class);
        doCallRealMethod()
                .when(postgresService)
                .delete(DATABASE_1);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            postgresService.delete(DATABASE_1);
        });
    }

    @Test
    public void getCreateStatement_succeeds() throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenReturn(connection);
        when(postgresService.getCreateDatabaseStatement(connection, DATABASE_1))
                .thenCallRealMethod();

        /* test */
        postgresService.getCreateDatabaseStatement(connection, DATABASE_1);
    }

    @Test
    public void getDeleteStatement_succeeds() throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/" + DATABASE_1_INTERNALNAME, postgresProperties);
        when(postgresService.open(anyString(), any()))
                .thenReturn(connection);
        when(postgresService.getDeleteDatabaseStatement(connection, DATABASE_1))
                .thenCallRealMethod();

        /* test */
        postgresService.getDeleteDatabaseStatement(connection, DATABASE_1);
    }
}
