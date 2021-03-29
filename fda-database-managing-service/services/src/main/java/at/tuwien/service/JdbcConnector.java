package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseMalformedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract void create(Container container, DatabaseCreateDto createDto) throws DatabaseConnectionException, DatabaseMalformedException;

    abstract PreparedStatement getCreateDatabaseStatement(Connection connection, DatabaseCreateDto createDto)
            throws SQLException;

}
