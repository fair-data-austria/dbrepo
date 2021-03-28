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

    public Database create(Container container, DatabaseCreateDto createDto) throws DatabaseConnectionException, DatabaseMalformedException {
        final Connection connection;
        try {
            connection = open("jdbc:postgresql://" + container.getName() + ":"
                    + container.getImage().getDefaultPort() + "/", postgresProperties);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getCreateDatabaseStatement(connection, createDto);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
        final Database database = new Database();
        database.setContainer(container);
        database.setName(createDto.getName());
        return databaseRepository.save(database);
    }


    @Override
    PreparedStatement getCreateDatabaseStatement(Connection connection, DatabaseCreateDto createDto)
            throws SQLException {
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE DATABASE ")
                .append(createDto.getName());
        queryBuilder.append(";");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }
}
