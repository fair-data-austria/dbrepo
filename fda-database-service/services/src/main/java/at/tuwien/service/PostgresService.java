package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Service
public class PostgresService extends JdbcConnector {

    private final Properties postgresProperties;
    private final DatabaseMapper databaseMapper;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public PostgresService(Properties postgresProperties, DatabaseMapper databaseMapper,
                           DatabaseRepository databaseRepository) {
        this.postgresProperties = postgresProperties;
        this.databaseMapper = databaseMapper;
        this.databaseRepository = databaseRepository;
    }

    @Override
    void create(Database database) throws DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/postgres";
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getCreateDatabaseStatement(connection, database);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
            throw new DatabaseMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }

    @Override
    void delete(Database database) throws DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/postgres";
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getDeleteDatabaseStatement(connection, database);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax or already exists");
            throw new DatabaseMalformedException("The SQL statement seems to contain invalid syntax or already exists", e);
        }
    }


    @Override
    PreparedStatement getCreateDatabaseStatement(Connection connection, Database database)
            throws SQLException {
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE DATABASE ")
                .append(database.getInternalName());
        queryBuilder.append(";");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled create db query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }

    @Override
    PreparedStatement getDeleteDatabaseStatement(Connection connection, Database database) throws SQLException {
        final StringBuilder queryBuilder = new StringBuilder()
                .append("DROP DATABASE ")
                .append(database.getInternalName());
        queryBuilder.append(";");
        final String deleteQuery = queryBuilder.toString();
        log.debug("compiled delete db query as \"{}\"", deleteQuery);
        return connection.prepareStatement(deleteQuery);
    }
}
