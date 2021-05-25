package at.tuwien.service;


import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DataProcessingException;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws DataProcessingException;

    abstract String insertStatement(List<Map<String, Object>> processedData, Table t, List<String> headers);

    abstract PreparedStatement getDeleteStatement(Connection connection, Table table) throws DataProcessingException;
}
