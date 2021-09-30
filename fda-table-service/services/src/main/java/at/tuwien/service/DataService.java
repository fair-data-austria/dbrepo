package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;

import java.sql.Timestamp;

public interface DataService {

    /**
     * Find a table by database-table id pair
     *
     * @param databaseId The database-table id pair.
     * @param tableId    The database-table id pair.
     * @return The table.
     * @throws TableNotFoundException    The table was not found.
     * @throws DatabaseNotFoundException The database was not found.
     */
    Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException;

    /**
     * Insert data from a file into a table of a database
     *
     * @param databaseId The database.
     * @param tableId    The table.
     * @param data       The null element and delimiter.
     * @throws TableNotFoundException     The table does not exist in the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DatabaseNotFoundException  The database does not exist in the metdata database.
     * @throws FileStorageException       The CSV could not be parsed.
     */
    void insertCsv(Long databaseId, Long tableId, TableInsertDto data)
            throws TableNotFoundException, ImageNotSupportedException, DatabaseNotFoundException, FileStorageException,
            TableMalformedException;

    /**
     * Insert data from AMQP client into a table of a database
     *
     * @param table The table.
     * @param data  The data.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     */
    void insert(Table table, TableCsvDto data) throws ImageNotSupportedException, TableMalformedException;

    /**
     * Select all data known in the database-table id tuple at a given time and return a page of specific size
     *
     * @param databaseId The database-table id tuple.
     * @param tableId    The database-table id tuple.
     * @param timestamp  The given time.
     * @param page       The page.
     * @param size       The page size.
     * @return The select all data result
     * @throws TableNotFoundException      The table was not found in the metadata database.
     * @throws DatabaseNotFoundException   The database was not found in the remote database.
     * @throws ImageNotSupportedException  The image is not supported.
     * @throws DatabaseConnectionException The connection to the remote database was unsuccessful.
     */
    QueryResultDto selectAll(Long databaseId, Long tableId, Timestamp timestamp, Integer page, Integer size) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException;
}
