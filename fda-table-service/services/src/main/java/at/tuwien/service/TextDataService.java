package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.Instant;

public interface TextDataService {

    /**
     * Reads a file and location into our data structure for a given table id
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param location   The location of the file.
     * @return Our data structure with data in it on success.
     * @throws IOException  File not readable.
     * @throws CsvException Problem with parsing the csv.
     */
    TableCsvDto read(Long databaseId, Long tableId, String location) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException;

    /**
     * Reads a file and location into our data structure for a given table id
     *
     * @param databaseId   The database id.
     * @param tableId      The table id.
     * @param location     The location of the file.
     * @param separator    The csv separator.
     * @param skipHeader   The indicator if the csv contains a header.
     * @param nullElement  The null element.
     * @param falseElement The false element.
     * @param trueElement  The true element.
     * @return Our data structure with data in it on success.
     * @throws IOException
     * @throws CsvException
     * @throws TableNotFoundException
     * @throws DatabaseNotFoundException
     */
    TableCsvDto read(Long databaseId, Long tableId, String location, Character separator, Boolean skipHeader, String nullElement,
                     String falseElement, String trueElement) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException;

    /**
     * Export csv from a database-table id tuple for a given timestamp
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param timestamp  The timestamp.
     * @return The exported csv.
     * @throws DatabaseNotFoundException The database is not found in the metadata database.
     * @throws TableNotFoundException    The table is not found in the metadata database.
     */
    Resource write(Long databaseId, Long tableId, Instant timestamp) throws DatabaseNotFoundException, TableNotFoundException, ImageNotSupportedException, FileStorageException, DatabaseConnectionException, TableMalformedException, PaginationException;

    /**
     * Export csv from a database-table id tuple as of now
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @return The exported csv.
     * @throws DatabaseNotFoundException The database is not found in the metadata database.
     * @throws TableNotFoundException    The table is not found in the metadata database.
     */
    Resource write(Long databaseId, Long tableId) throws DatabaseNotFoundException, TableNotFoundException, ImageNotSupportedException, FileStorageException, DatabaseConnectionException, TableMalformedException, PaginationException;
}
