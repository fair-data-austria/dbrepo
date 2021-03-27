package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.PostgresTableMapper;
import at.tuwien.repository.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

@Log4j2
@Service
public class PostgresService extends JdbcConnector {

    private final Properties postgresProperties;
    private final TableRepository tableRepository;
    private final PostgresTableMapper postgresTableMapper;

    @Autowired
    protected PostgresService(Properties postgresProperties, TableRepository tableRepository,
                              PostgresTableMapper postgresTableMapper) {
        this.postgresProperties = postgresProperties;
        this.tableRepository = tableRepository;
        this.postgresTableMapper = postgresTableMapper;
    }

    public Table createTable(Database database, TableCreateDto createDto) throws DatabaseConnectionException, TableMalformedException {
        final Connection connection;
        try {
            connection = open("jdbc:postgresql://" + database.getContainer().getIpAddress() + ":"
                    + database.getContainer().getImage().getDefaultPort() + "/" + database.getName(), postgresProperties);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        try {
            final PreparedStatement statement = getCreateTableStatement(connection, createDto);
            statement.execute();
        } catch (SQLException e) {
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
        final Table table = new Table();
        table.setDatabase(database);
        table.setDescription(createDto.getDescription());
        return tableRepository.save(table);
    }

    @Override
    final PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws SQLException {
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE TABLE ")
                .append(createDto.getName());
        final Iterator<String> columnIterator = postgresTableMapper.columnCreateDtoArrayToStringArray(createDto.getColumns())
                .listIterator();
        while(columnIterator.hasNext()) {
            queryBuilder.append(columnIterator.next());
            if (columnIterator.hasNext()) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(";");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }
}
