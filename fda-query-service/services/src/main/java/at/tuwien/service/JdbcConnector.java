package at.tuwien.service;


import at.tuwien.entity.Database;
import at.tuwien.entity.Query;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateQueryStoreStatement(Connection connection) throws SQLException;

    abstract List<Query> getQueries(Database database) throws SQLException;

}
