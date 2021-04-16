package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.columns.ColumnCreateDto;
import at.tuwien.dto.table.columns.ColumnTypeDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.PostgresTableMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.TableRepository;
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
    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final PostgresTableMapper postgresTableMapper;

    @Autowired
    protected PostgresService(Properties postgresProperties, TableRepository tableRepository,
                              TableMapper tableMapper, PostgresTableMapper postgresTableMapper) {
        this.postgresProperties = postgresProperties;
        this.tableRepository = tableRepository;
        this.tableMapper = tableMapper;
        this.postgresTableMapper = postgresTableMapper;
    }

    public void createTable(Database database, TableCreateDto createDto) throws DatabaseConnectionException, TableMalformedException {
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
            final PreparedStatement statement = getCreateTableStatement(connection, createDto);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }

    @Override
    final PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws SQLException {
        log.debug("create table columns {}", Arrays.toString(createDto.getColumns()));
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE TABLE ")
                .append(tableMapper.columnNameToString(createDto.getName()))
                .append(" (");
        final Iterator<String> columnIterator = mockAnalyzeService(createDto.getColumns())
                .listIterator();
        while(columnIterator.hasNext()) {
            queryBuilder.append(columnIterator.next());
            if (columnIterator.hasNext()) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(");");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }

    @Override
    public void insert(List<Map<String, Object>> processedData, Table t) {
        log.debug("insert data into {}", t.getName());
        StringBuilder queryBuilder = new StringBuilder()
                .append("INSERT INTO ")
                .append(tableMapper.columnNameToString(t.getName()))
                .append(" (");
        for
    }

    /**
     * FIXME THIS IS REMOVED IN SPRINT 2
     *
     * @param columnDto
     * @return
     */
    private List<String> mockAnalyzeService(ColumnCreateDto[] columnDto) {
        final List<String> columns = new LinkedList<>();
        for (ColumnCreateDto column : columnDto) {
            final StringBuilder columnBuilder = new StringBuilder()
                    .append(tableMapper.columnNameToString(column.getName()))
                    .append(" ")
                    .append(mockDataTypeAnalyze(column.getType()))
                    .append(mockCheckExpression(column))
                    .append(!column.getNullAllowed() ? " NOT" : "")
                    .append(" NULL")
                    .append(mockForeignKey(column))
                    .append(column.getPrimaryKey() ? " PRIMARY KEY" : "");
            columns.add(columnBuilder.toString());
        }
        return columns;
    }

    /**
     * FIXME THIS IS REMOVED IN SPRINT 2
     *
     * @param type
     * @return
     */
    private String mockDataTypeAnalyze(ColumnTypeDto type) {
        switch (type) {
            case TEXT:
            case STRING:
                return "TEXT";
            case NUMBER:
                return "DOUBLE PRECISION";
            case DATE:
                return "TIMESTAMP";
            case BLOB:
                return "BYTEA";
            case BOOLEAN:
                return "BOOLEAN";
            default:
                throw new IllegalArgumentException("Unable to match datatype with PostgreSQL");
        }
    }

    /**
     * FIXME THIS IS REMOVED IN SPRINT 2
     *
     * @param column
     * @return
     */
    private String mockCheckExpression(ColumnCreateDto column) {
        if (column.getCheckExpression() == null || column.getCheckExpression().isEmpty()) {
            return "";
        }
        final String columnName = tableMapper.columnNameToString(column.getName());
        final String expression = column.getCheckExpression().replace(column.getName(), columnName);
        return " CHECK ( " + expression + " )";
    }

    private String mockForeignKey(ColumnCreateDto column) {
        if (column.getForeignKey() == null || column.getForeignKey().isEmpty()) {
            return "";
        }
        return " FOREIGN KEY " + column.getForeignKey();
    }
}
