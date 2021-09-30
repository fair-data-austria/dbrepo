package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface DatabaseConnector {
    /**
     * Open a new database connection for a database
     *
     * @param database The database
     * @return The database connection
     * @throws SQLException               Error in the Syntax
     * @throws ImageNotSupportedException The credentials for the image seems not as expected (username, password)
     */
    DSLContext open(Database database) throws SQLException, ImageNotSupportedException;

    /**
     * Create a new database from a entity and create information
     *
     * @param database  The entity
     * @param createDto The create information
     * @throws SQLException                  Error in the Syntax
     * @throws ArbitraryPrimaryKeysException The primary keys provided are not supported.
     * @throws ImageNotSupportedException    The image is not supported.
     * @throws TableMalformedException       The resulting table by the mapper is malformed.
     */
    void create(Database database, TableCreateDto createDto) throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException;

    /**
     * Insert data inside a csv document into a table
     *
     * @param table The table
     * @param data  The csv document
     * @throws SQLException               Error in the syntax.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The resulting table by the mapper is malformed.
     */
    void insertCsv(Table table, TableCsvDto data) throws SQLException, ImageNotSupportedException, TableMalformedException;

    /**
     * Delete a table
     *
     * @param table The table.
     * @throws SQLException               Error in the syntax.
     * @throws ImageNotSupportedException The image is not supported.
     */
    void delete(Table table) throws SQLException, ImageNotSupportedException;
}
