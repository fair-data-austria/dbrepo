package at.tuwien.service;


import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.mapper.TableMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
public abstract class JdbcConnector {

    final TableMapper tableMapper;

    @Autowired
    protected JdbcConnector(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    abstract PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws DataProcessingException;

    abstract PreparedStatement getInsertStatement(Connection connection, List<Map<String, Object>> processedData, Table table, List<String> headers) throws DataProcessingException;

    abstract PreparedStatement getDeleteStatement(Connection connection, Table table) throws DataProcessingException;

    protected String selectStatement(Table t) {
        log.debug("selecting data from {}", t.getName());

        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT ");
        for (TableColumn tc : t.getColumns()) {
            queryBuilder.append(tc.getInternalName() + ",");
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" FROM " + t.getInternalName());
        log.debug(queryBuilder.toString());
        return queryBuilder.toString();
    }

    protected List<String> mockAnalyzeService(ColumnCreateDto[] columnDto) {
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

    protected String mockDataTypeAnalyze(ColumnTypeDto type) {
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

    protected String mockCheckExpression(ColumnCreateDto column) {
        if (column.getCheckExpression() == null || column.getCheckExpression().isEmpty()) {
            return "";
        }
        final String columnName = tableMapper.columnNameToString(column.getName());
        final String expression = column.getCheckExpression().replace(column.getName(), columnName);
        return " CHECK ( " + expression + " )";
    }

    protected String mockForeignKey(ColumnCreateDto column) {
        if (column.getForeignKey() == null || column.getForeignKey().isEmpty()) {
            return "";
        }
        return " FOREIGN KEY " + column.getForeignKey();
    }
}
