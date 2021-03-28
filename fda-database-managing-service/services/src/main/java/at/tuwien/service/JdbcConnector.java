package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.dto.table.TableCreateDto;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Service
public abstract class JdbcConnector {

    protected Connection open(String url, Properties properties) throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

    abstract PreparedStatement getCreateDatabaseStatement(Connection connection, DatabaseCreateDto createDto)
            throws SQLException;

}
