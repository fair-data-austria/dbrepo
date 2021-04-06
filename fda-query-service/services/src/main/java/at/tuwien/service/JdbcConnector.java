package at.tuwien.service;


import java.sql.*;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateQueryStoreStatement(Connection connection) throws SQLException;

}
