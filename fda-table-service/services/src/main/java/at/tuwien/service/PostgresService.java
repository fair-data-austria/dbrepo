package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @apiNote The PostgreSQL service requires a backdoor admin account with username="postgres" and password="postgres"
 */
@Service
public class PostgresService extends JdbcConnector {

    private final Properties postgresProperties;

    @Autowired
    protected PostgresService(Properties postgresProperties) throws ClassNotFoundException {
        this.postgresProperties = postgresProperties;
    }

    public void createTable(Container container, Database database, TableCreateDto createDto) throws DatabaseConnectionException, TableMalformedException {
        final Connection connection;
        try {
            connection = open("jdbc:postgresql://" + container.getIpAddress() + ":"
                    + container.getImage().getDefaultPort() + "/" + database.getName(), postgresProperties);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        // get sql statement, todo
        final String sql = "SELECT 1";
        // create database
        final Statement statement;
        try {
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }

}
