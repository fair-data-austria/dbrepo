package at.tuwien.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

}
