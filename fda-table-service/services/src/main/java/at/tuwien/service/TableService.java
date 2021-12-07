package at.tuwien.service;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TableService {

    /**
     * Select all tables from the metadata database.
     *
     * @return The list of tables.
     */
    List<Table> findAll();

    /**
     * Find all tables for a given database id.
     *
     * @param databaseId The database id.
     * @return Return a list of all tables for this database id.
     * @throws DatabaseNotFoundException The database was not found in the metadata database.
     */
    List<Table> findAllForDatabaseId(Long databaseId) throws DatabaseNotFoundException;

    /**
     * Deletes a table for a fiven database-table id pair.
     *
     * @param databaseId The database-table id pair.
     * @param tableId    The database-table id pair.
     * @throws TableNotFoundException     The table was not found in the metadata database.
     * @throws DatabaseNotFoundException  The database was not found in the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DataProcessingException    The deletion did not work.
     */
    void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException;

    /**
     * Find a table by database-table id pair
     *
     * @param databaseId The database-table id pair.
     * @param tableId    The database-table id pair.
     * @return The table.
     * @throws TableNotFoundException    The table was not found in the metadata database.
     * @throws DatabaseNotFoundException The database was not found in the metadata database.
     */
    Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException;

    /**
     * Creates a table for a database id with given schema as data
     *
     * @param databaseId The database id.
     * @param createDto  The schema (as data)
     * @return The created table.
     * @throws ImageNotSupportedException    The image is not supported.
     * @throws DatabaseNotFoundException     The database was not found in the metadata database.
     * @throws DataProcessingException       The remote database engine resulted in some error.
     * @throws ArbitraryPrimaryKeysException The primary keys are configured wrong.
     * @throws TableMalformedException       The table seems malformed by the mapper.
     */
    Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException;

    Database findDatabase(Long id) throws DatabaseNotFoundException;

    TableCsvDto readCsv(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException,
            ArrayIndexOutOfBoundsException;
}
