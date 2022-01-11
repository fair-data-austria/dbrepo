package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.Instant;

public interface CommaValueService {

    /**
     * Reads a data source from the location into the table with given id inside a database with given id and returns the data.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param location   The location.
     * @return The data from the location.
     * @throws TableNotFoundException    Table with id not found.
     * @throws DatabaseNotFoundException Database with id not found.
     * @throws FileStorageException      File could not be processed.
     */
    TableCsvDto read(Long databaseId, Long tableId, String location) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException;

    /**
     * Reads a data source from the location into the table with given id inside a database with given id and returns the data.
     *
     * @param databaseId   The database id.
     * @param tableId      The table id.
     * @param location     The location.
     * @param separator    Data separator.
     * @param skipHeader   Indication if the first line contains a header.
     * @param nullElement  The element representing {@link null}.
     * @param falseElement The element representing {@link false}.
     * @param trueElement  The element representing {@link true}.
     * @return The data from the location.
     * @throws TableNotFoundException    Table with id not found.
     * @throws DatabaseNotFoundException Database with id not found.
     * @throws FileStorageException      File could not be processed.
     */
    TableCsvDto read(Long databaseId, Long tableId, String location, Character separator, Boolean skipHeader, String nullElement,
                     String falseElement, String trueElement) throws TableNotFoundException, DatabaseNotFoundException, FileStorageException;

    /**
     * Exports a table to a file by given database and table id for a specific point in time.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param timestamp  The point in time.
     * @return The export.
     * @throws TableNotFoundException      The table was not found.
     * @throws DatabaseConnectionException The connection to the database failed.
     * @throws DatabaseNotFoundException   The database was not found.
     * @throws ImageNotSupportedException  The image is not supported. Currently only MariaDB is supported.
     * @throws PaginationException         The pagination failed.
     * @throws FileStorageException        The table could not be exported.
     * @throws TableMalformedException     The table is malformed.
     */
    InputStreamResource export(Long databaseId, Long tableId, Instant timestamp) throws TableNotFoundException,
            DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException,
            PaginationException, FileStorageException, TableMalformedException;

    /**
     * Exports a table to a file by given database and table id for a specific point in time.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @return The export.
     * @throws TableNotFoundException      The table was not found.
     * @throws DatabaseConnectionException The connection to the database failed.
     * @throws DatabaseNotFoundException   The database was not found.
     * @throws ImageNotSupportedException  The image is not supported. Currently only MariaDB is supported.
     * @throws PaginationException         The pagination failed.
     * @throws FileStorageException        The table could not be exported.
     * @throws TableMalformedException     The table is malformed.
     */
    InputStreamResource export(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException, FileStorageException;
}
