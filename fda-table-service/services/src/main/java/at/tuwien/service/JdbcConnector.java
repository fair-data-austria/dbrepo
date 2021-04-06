package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;

import java.sql.*;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateTableStatement(Connection connection, TableCreateDto createDto) throws SQLException;

}
