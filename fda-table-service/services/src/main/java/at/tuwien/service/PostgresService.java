package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.columns.ColumnCreateDto;
import at.tuwien.dto.table.columns.ColumnTypeDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.entity.TableColumn;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.PostgresTableMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.model.QueryResult;
import at.tuwien.repository.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private Connection getConnection(Database database) throws DatabaseConnectionException {
        Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/" + database.getInternalName();
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        return connection;
    }

    public void createTable(Database database, TableCreateDto createDto) throws DatabaseConnectionException, TableMalformedException {
        try {
            final PreparedStatement statement = getCreateTableStatement(getConnection(database), createDto);
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
            throw new TableMalformedException("The SQL statement seems to contain invalid syntax", e);
        }
    }

    public QueryResult insertIntoTable(Database database, Table t, List<Map<String, Object>> processedData, List<String> headers) {
        try{
            Connection connection = getConnection(database);
            PreparedStatement statement = connection.prepareStatement(insertStatement(processedData, t, headers));
            statement.execute();
            return getAllRows(database,t);
        } catch(DatabaseConnectionException e) {
            log.error("Problem with connecting to the database while selecting from Querystore");
        } catch(SQLException e) {
            log.debug(e.getMessage());
            log.error("The SQL statement seems to contain invalid syntax");
        }
        return null;
    }

    /**
     * FIXME IN Sprint 2
     *
     * @param database
     * @param t
     * @return
     */
    public QueryResult getAllRows(Database database, Table t) {
        try{
            Connection connection = getConnection(database);
            PreparedStatement statement = connection.prepareStatement(selectStatement(t));
            ResultSet result = statement.executeQuery();
            QueryResult qr = new QueryResult();
            List<Map<String,Object>> res = new ArrayList<>();
            while(result.next()) {
                Map<String,Object> r = new HashMap<>();
                for(TableColumn tc : t.getColumns()) {
                    r.put(tc.getName(), result.getString(tc.getInternalName()));
                }
                res.add(r);
            }
            qr.setResult(res);
            return qr;
        } catch(DatabaseConnectionException e) {
            log.error("Problem with connecting to the database while selecting from Querystore");
        } catch(SQLException e) {
            log.debug(e.getMessage());
            log.error("The SQL statement seems to contain invalid syntax");
        }
        return null;
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

    public String selectStatement(Table t) {
        log.debug("selecting data from {}", t.getName());

        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT ");
        for( TableColumn tc: t.getColumns()) {
            queryBuilder.append(tc.getInternalName()+",");
        }
        queryBuilder.deleteCharAt(queryBuilder.length()-1);
        queryBuilder.append(" FROM " + t.getInternalName());
        log.debug(queryBuilder.toString());
        return queryBuilder.toString();
    }

    /**
     * FIXME THIS IS REMOVED IN SPRINT 2
     * @param processedData
     * @param t
     * @return
     */
    @Override
    public String insertStatement(List<Map<String, Object>> processedData, Table t, List<String> headers) {
        log.debug("insertStatement data into {}", t.getName());
        StringBuilder queryBuilder = new StringBuilder()
                .append("INSERT INTO ")
                .append(tableMapper.columnNameToString(t.getInternalName()))
                .append("(");
        for(String h : headers) {
            queryBuilder.append(t.getColumns().stream().filter(x -> x.getName().equals(h)).findFirst().get().getInternalName()+",");
        }
        queryBuilder.deleteCharAt(queryBuilder.length()-1);
        queryBuilder.append(") VALUES ");

        for (Map<String, Object> m : processedData ) {
            queryBuilder.append("(");
            for ( Map.Entry<String,Object> entry : m.entrySet()) {
                TableColumn tc = t.getColumns().stream().filter(x -> x.getName().equals(entry.getKey())).findFirst().get();
                if(tc.getColumnType().toString().equals("STRING")) {
                    queryBuilder.append("'" + entry.getValue() + "'" + ",");
                }
                else {
                    queryBuilder.append(entry.getValue()+",");
                }
            }
            queryBuilder.deleteCharAt(queryBuilder.length()-1);
            queryBuilder.append("),");
        }
        queryBuilder.deleteCharAt(queryBuilder.length()-1);
        queryBuilder.append(";");
        log.debug(queryBuilder.toString());
        return queryBuilder.toString();
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
