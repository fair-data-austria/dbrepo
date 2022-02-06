package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import org.springframework.core.io.InputStreamResource;

public interface CommaValueService {

    /**
     * Reads a data source from the location into the table with given id inside a database with given id and returns the data.
     *
     * @param containerId The container id.
     * @param databaseId  The database id.
     * @param tableId     The table id.
     * @param location    The location.
     * @return The data from the location.
     * @throws TableNotFoundException    Table with id not found.
     * @throws DatabaseNotFoundException Database with id not found.
     * @throws FileStorageException      File could not be processed.
     */
    TableCsvDto read(Long containerId, Long databaseId, Long tableId, String location) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException, ContainerNotFoundException;

    /**
     * Reads a data source from the location into the table with given id inside a database with given id and returns the data.
     *
     * @param containerId  The container id.
     * @param databaseId   The database id.
     * @param tableId      The table id.
     * @param location     The location.
     * @param separator    Data separator.
     * @param skipLines    Skip first n lines.
     * @param nullElement  The element representing {@link null}.
     * @param falseElement The element representing {@link false}.
     * @param trueElement  The element representing {@link true}.
     * @return The data from the location.
     * @throws TableNotFoundException    Table with id not found.
     * @throws DatabaseNotFoundException Database with id not found.
     * @throws FileStorageException      File could not be processed.
     */
    TableCsvDto read(Long containerId, Long databaseId, Long tableId, String location, Character separator, Long skipLines, String nullElement,
                     String falseElement, String trueElement) throws TableNotFoundException, DatabaseNotFoundException, FileStorageException, ContainerNotFoundException;

//    /**
//     * Exports a table to a file by given database and table id for a specific point in time.
//     *
//     * @param containerId The container id.
//     * @param databaseId  The database id.
//     * @param tableId     The table id.
//     * @param timestamp   The point in time.
//     * @return The export.
//     * @throws TableNotFoundException      The table was not found.
//     * @throws DatabaseConnectionException The connection to the database failed.
//     * @throws DatabaseNotFoundException   The database was not found.
//     * @throws ImageNotSupportedException  The image is not supported. Currently only MariaDB is supported.
//     * @throws PaginationException         The pagination failed.
//     * @throws FileStorageException        The table could not be exported.
//     * @throws TableMalformedException     The table is malformed.
//     */
//    InputStreamResource export(Long containerId, Long databaseId, Long tableId, Instant timestamp) throws TableNotFoundException,
//            DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException,
//            PaginationException, FileStorageException, TableMalformedException, ContainerNotFoundException;

//    /**
//     * Exports a table to a file by given database and table id for a specific point in time.
//     *
//     * @param containerId The container id.
//     * @param databaseId  The database id.
//     * @param tableId     The table id.
//     * @return The export.
//     * @throws TableNotFoundException      The table was not found.
//     * @throws DatabaseConnectionException The connection to the database failed.
//     * @throws DatabaseNotFoundException   The database was not found.
//     * @throws ImageNotSupportedException  The image is not supported. Currently only MariaDB is supported.
//     * @throws PaginationException         The pagination failed.
//     * @throws FileStorageException        The table could not be exported.
//     * @throws TableMalformedException     The table is malformed.
//     */
//    InputStreamResource export(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException,
//            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException, FileStorageException, ContainerNotFoundException;
}
