package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Table;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws SQLException;

    abstract String insert(List<Map<String, Object>> processedData, Table t, List<String> headers);
}
