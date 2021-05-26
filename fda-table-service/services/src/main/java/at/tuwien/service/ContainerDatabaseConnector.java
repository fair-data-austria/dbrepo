package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface ContainerDatabaseConnector {

    /**
     * Opens a new connection to the user database (running in a Docker container)
     *
     * @param database The user database.
     * @return A connection.
     * @throws DatabaseConnectionException When the connection could not be established.
     */
    Connection getConnection(Database database) throws DatabaseConnectionException;

    /**
     * Creates a new table with the given table specification from the front-end
     *
     * @param database           The database the table should be created in.
     * @param tableSpecification The table specification.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws TableMalformedException     When the specification was not transformable.
     * @throws DataProcessingException     When the database returned some error.
     */
    void createTable(Database database, TableCreateDto tableSpecification) throws DatabaseConnectionException, TableMalformedException, DataProcessingException;

    /**
     * Inserts data into an existing table (of a user database running in a Docker container)
     *
     * @param database The database.
     * @param table    The table.
     * @param data     The data.
     * @param headers  List of headers from the data.
     * @return The parsed data.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws DataProcessingException     When the database returned some error.
     */
    QueryResultDto insertIntoTable(Database database, Table table, List<Map<String, Object>> data, List<String> headers) throws DatabaseConnectionException, DataProcessingException;

    /**
     * Retrieve all rows of a table (of a user database running in a Docker container).
     *
     * @param database The database.
     * @param table    The table.
     * @return The parsed rows.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws DataProcessingException     When the database returned some error.
     */
    QueryResultDto getAllRows(Database database, Table table) throws DatabaseConnectionException, DataProcessingException;

    /**
     * Deletes a table (of a user database running in a Docker container).
     *
     * @param table The table.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws TableMalformedException     When the specification was not transformable.
     * @throws DataProcessingException     When the database returned some error.
     */
    void deleteTable(Table table) throws DatabaseConnectionException, TableMalformedException, DataProcessingException;
}
