package at.tuwien.service;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;

import java.util.List;

public interface TableService {

    /**
     * Select all tables from the metadata database.
     *
     * @return The list of tables.
     */
    List<Table> findAll(Long containerId, Long databaseId) throws DatabaseNotFoundException;

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
    void deleteTable(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, ContainerNotFoundException;

    /**
     * Find a table by database-table id pair
     *
     * @param databaseId The database-table id pair.
     * @param tableId    The database-table id pair.
     * @return The table.
     * @throws TableNotFoundException    The table was not found in the metadata database.
     * @throws DatabaseNotFoundException The database was not found in the metadata database.
     */
    Table findById(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException, ContainerNotFoundException;

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
    Table createTable(Long containerId, Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException, TableNameExistsException, ContainerNotFoundException, UserNotFoundException;

}
