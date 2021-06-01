package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.PostgresTableMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Log4j2
@Service
public class PostgresService extends JdbcConnector implements ContainerDatabaseConnector {

    private final Properties postgresProperties;
    private final TableMapper tableMapper;

    @Autowired
    protected PostgresService(Properties postgresProperties, TableMapper tableMapper) {
        super(tableMapper);
        this.postgresProperties = postgresProperties;
        this.tableMapper = tableMapper;
    }

    private Connection getConnection(Database database) throws DatabaseConnectionException {
        Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/" + database.getInternalName();
        try {
            connection = DriverManager.getConnection(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running at: " + URL, e);
        }
        return connection;
    }

    @Override
    public void createTable(Database database, TableCreateDto createDto) throws DatabaseConnectionException, TableMalformedException, DataProcessingException {
        try {
            final PreparedStatement statement = getCreateTableStatement(getConnection(database), createDto);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }

    @Override
    public QueryResultDto insertIntoTable(Database database, Table table, List<Map<String, Object>> processedData, List<String> headers) throws DatabaseConnectionException, DataProcessingException {
        try {
            final PreparedStatement statement = getInsertStatement(getConnection(database), processedData, table, headers);
            statement.execute();
            return getAllRows(database, table);
        } catch (DatabaseConnectionException e) {
            log.error("Problem with connecting to the database while selecting from query store: {}", e.getMessage());
            throw new DatabaseConnectionException("database connection problem with query store", e);
        } catch (SQLException | NullPointerException e) {
            log.error("The SQL statement seems to contain invalid syntax: {}", e.getMessage());
            throw new DataProcessingException("invalid syntax", e);
        }
    }

    public QueryResultDto getAllRows(Database database, Table t) throws DatabaseConnectionException, DataProcessingException {
        try {
            Connection connection = getConnection(database);
            PreparedStatement statement = connection.prepareStatement(selectStatement(t));
            ResultSet result = statement.executeQuery();
            QueryResultDto qr = new QueryResultDto();
            List<Map<String, Object>> res = new ArrayList<>();
            while (result.next()) {
                Map<String, Object> r = new HashMap<>();
                for (TableColumn tc : t.getColumns()) {
                    if (ColumnTypeDto.valueOf(tc.getColumnType()).equals(ColumnTypeDto.NUMBER)) {
                        r.put(tc.getName(), result.getDouble(tc.getInternalName()));
                    } else if (ColumnTypeDto.valueOf(tc.getColumnType()).equals(ColumnTypeDto.BOOLEAN)) {
                        r.put(tc.getName(), result.getBoolean(tc.getInternalName()));
                    } else {
                        r.put(tc.getName(), result.getString(tc.getInternalName()));
                    }
                }
                res.add(r);
            }
            log.debug("assembled result: {}", res);
            qr.setResult(res);
            return qr;
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax: {}", e.getMessage());
            throw new DataProcessingException("invalid syntax", e);
        }
    }

    @Override
    public final PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws DataProcessingException {
        log.debug("create table columns {}", Arrays.asList(createDto.getColumns()));
        final StringBuilder queryBuilder = new StringBuilder()
                .append("CREATE TABLE ")
                .append(tableMapper.columnNameToString(createDto.getName()))
                .append(" (");
        final Iterator<String> columnIterator = mockAnalyzeService(createDto.getColumns())
                .listIterator();
        while (columnIterator.hasNext()) {
            queryBuilder.append(columnIterator.next());
            if (columnIterator.hasNext()) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(");");
        final String createQuery = queryBuilder.toString();
        log.debug("compiled query as \"{}\"", createQuery);
        try {
            return connection.prepareStatement(createQuery);
        } catch (SQLException e) {
            log.error("invalid syntax: {}", e.getMessage());
            throw new DataProcessingException("invalid syntax", e);
        }
    }

    @Override
    public PreparedStatement getInsertStatement(Connection connection, List<Map<String, Object>> processedData, Table t, List<String> headers) throws DataProcessingException {
        log.debug("insert table name: {}", t.getInternalName());
        StringBuilder queryBuilder = new StringBuilder()
                .append("INSERT INTO ")
                .append(tableMapper.columnNameToString(t.getInternalName()))
                .append("(");
        for (String h : headers) {
            // FIXME empty columns list in table produces nullpointer exception
            queryBuilder.append(t.getColumns().stream().filter(x -> x.getName().equals(h)).findFirst().get().getInternalName() + ",");
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(") VALUES ");

        // FIXME: no rows in processed data produce invalid syntax, but no exception thrown
        for (Map<String, Object> m : processedData) {
            queryBuilder.append("(");
            // FIXME: no rows in processed data produce invalid syntax, but no exception thrown
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                TableColumn tc = t.getColumns().stream().filter(x -> x.getName().equals(entry.getKey())).findFirst().get();
                if (tc.getColumnType().toString().equals("STRING") || tc.getColumnType().equals("TEXT")) {
                    queryBuilder.append("'" + entry.getValue() + "'" + ",");
                } else {
                    queryBuilder.append(entry.getValue() + ",");
                }
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append("),");
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(";");
        log.debug(queryBuilder.toString());
        try {
            return connection.prepareStatement(queryBuilder.toString());
        } catch (SQLException e) {
            log.error("invalid syntax: {}", e.getMessage());
            throw new DataProcessingException("invalid syntax", e);
        }
    }

    @Override
    public void deleteTable(Table table) throws DatabaseConnectionException, TableMalformedException, DataProcessingException {
        try {
            final PreparedStatement statement = getDeleteStatement(getConnection(table.getDatabase()), table);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax or table not existing");
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax or table not existing", e);
        }
    }

    @Override
    final PreparedStatement getDeleteStatement(Connection connection, Table table) throws DataProcessingException {
        final StringBuilder deleteQuery = new StringBuilder("DROP TABLE ")
                .append(tableMapper.columnNameToString(table.getInternalName()))
                .append(";");
        log.debug("compiled delete table statement as {}", deleteQuery.toString());
        try {
            return connection.prepareStatement(deleteQuery.toString());
        } catch (SQLException e) {
            log.error("invalid syntax or not existing table: {}", e.getMessage());
            throw new DataProcessingException("invalid syntax or not existing table", e);
        }
    }
}
