package at.tuwien.service;

import at.tuwien.entity.Database;
import at.tuwien.exception.DatabaseConnectionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Log4j2
@Service
public class PostgresService extends JdbcConnector {

    private final Properties postgresProperties;

    @Autowired
    protected PostgresService(Properties postgresProperties) {
        this.postgresProperties = postgresProperties;
    }

    public void createQuerystore(Database database) throws DatabaseConnectionException {
        final Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/" + database.getInternalName();
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getCreateQueryStoreStatement(connection);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
        }
    }

    @Override
    PreparedStatement getCreateQueryStoreStatement(Connection connection) throws SQLException {
        log.debug("create querystore");
        final String createQuery="CREATE TABLE IF NOT EXISTS 'querystore' (" +
                "'id' serial PRIMARY KEY," +
                "'query' text," +
                "'query_normalized' text," +
                "'query_hash' text," +
                "'execution_timestamp' timestamp," +
                "'result_hash' text," +
                "'result_number' integer ," +
                "PRIMARY KEY( id )" +
                ");";
        log.debug("compiled query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }
}
