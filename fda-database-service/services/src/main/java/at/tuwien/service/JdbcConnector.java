package at.tuwien.service;

import at.tuwien.entities.database.Database;
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

    /**
     * Creates a new database in the Docker container.
     *
     * @param database The database
     * @throws DatabaseConnectionException In case the container is not reachable by JDBC
     * @throws DatabaseMalformedException In case the database e.g. exists already
     */
    abstract void create(Database database) throws DatabaseConnectionException, DatabaseMalformedException;

    /**
     * Deletes a database from the Docker container.
     *
     * @param database The database
     * @throws DatabaseConnectionException In case the container is not reachable by JDBC
     * @throws DatabaseMalformedException In case the database could not be deleted
     */
    abstract void delete(Database database) throws DatabaseConnectionException, DatabaseMalformedException;

    /**
     * Helper function that compiles a creation statement
     *
     * @param connection The JDBC connection
     * @param database The database name
     * @return A prepared statement
     * @throws SQLException In case the compiled query is invalid
     */
    abstract PreparedStatement getCreateDatabaseStatement(Connection connection, Database database)
            throws SQLException;

    /**
     * Helper function that compiles a delete statement
     *
     * @param connection The JDBC connection
     * @param database The database name
     * @return A prepared statement
     * @throws SQLException In case the compiled query is invalid
     */
    abstract PreparedStatement getDeleteDatabaseStatement(Connection connection, Database database)
            throws SQLException;

}
