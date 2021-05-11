package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseMalformedException;
import com.netflix.discovery.converters.Auto;
import org.apache.catalina.connector.Connector;
import org.h2.jdbc.JdbcConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@Transactional // rollback on each test
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostgresIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private PostgresService postgresService;

    @Autowired
    private Properties postgresProperties;

    private Connection CONNECTION;

    @BeforeEach
    public void beforeEach() throws SQLException {
        CONNECTION = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fda-test", postgresProperties);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", postgresProperties);
        final PreparedStatement statement = connection.prepareStatement("DROP DATABASE IF EXISTS " + DATABASE_1_INTERNALNAME + ";");
        statement.execute();
    }

    @Test
    public void create_succeeds() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        when(postgresService.open(anyString(), any()))
                .thenReturn(CONNECTION);
        when(postgresService.getCreateDatabaseStatement(CONNECTION, DATABASE_1))
                .thenCallRealMethod();
        doCallRealMethod()
                .when(postgresService)
                .create(DATABASE_1);
        /* test */
        postgresService.create(DATABASE_1);
    }

    @Test
    public void create_noConnection_fails() throws SQLException, DatabaseConnectionException, DatabaseMalformedException {
        when(postgresService.open(anyString(), any()))
                .thenThrow(SQLException.class);
        when(postgresService.getCreateDatabaseStatement(CONNECTION, DATABASE_1))
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
        when(postgresService.open(anyString(), any()))
                .thenReturn(CONNECTION);
        when(postgresService.getCreateDatabaseStatement(CONNECTION, DATABASE_1))
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
        when(postgresService.open(anyString(), any()))
                .thenThrow(SQLException.class);
        when(postgresService.getDeleteDatabaseStatement(CONNECTION, DATABASE_1))
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
        when(postgresService.open(anyString(), any()))
                .thenReturn(CONNECTION);
        when(postgresService.getDeleteDatabaseStatement(CONNECTION, DATABASE_1))
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
        when(postgresService.open(anyString(), any()))
                .thenReturn(CONNECTION);
        when(postgresService.getCreateDatabaseStatement(CONNECTION, DATABASE_1))
                .thenCallRealMethod();

        /* test */
        postgresService.getCreateDatabaseStatement(CONNECTION, DATABASE_1);
    }

    @Test
    public void getDeleteStatement_succeeds() throws SQLException {
        when(postgresService.open(anyString(), any()))
                .thenReturn(CONNECTION);
        when(postgresService.getDeleteDatabaseStatement(CONNECTION, DATABASE_1))
                .thenCallRealMethod();

        /* test */
        postgresService.getDeleteDatabaseStatement(CONNECTION, DATABASE_1);
    }
}
