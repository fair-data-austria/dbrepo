package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.repository.ContainerRepository;
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
    private final DatabaseRepository databaseRepository;

    @Autowired
    public PostgresService(Properties postgresProperties, DatabaseRepository databaseRepository) {
        this.postgresProperties = postgresProperties;
        this.databaseRepository = databaseRepository;
    }

    @Override
    void create(Container container, DatabaseCreateDto createDto) throws DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection;
        final String URL = "jdbc:postgresql://" + container.getName() + ":"
                + container.getImage().getDefaultPort() + "/postgres";
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getCreateDatabaseStatement(connection, createDto);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
            throw new DatabaseMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }


    @Override
    PreparedStatement getCreateDatabaseStatement(Connection connection, DatabaseCreateDto createDto)
            throws SQLException {
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE DATABASE ")
                .append(createDto.getName());
        queryBuilder.append(";");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled create db query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }
}
