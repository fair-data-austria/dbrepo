package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;

@Service
public interface QueryService {

    /**
     * Executes an arbitrary query on the database container. We allow the user to only view the data, therefore the
     * default "mariadb" user is allowed read-only access "SELECT".
     *
     * @param databaseId The database id.
     * @param query      The query.
     * @return The result.
     * @throws TableNotFoundException
     * @throws QueryStoreException
     * @throws QueryMalformedException
     * @throws DatabaseNotFoundException
     * @throws ImageNotSupportedException
     */
    QueryResultDto execute(Long containerId, Long databaseId, ExecuteStatementDto query) throws TableNotFoundException,
            QueryStoreException, QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException;

    /**
     * Select all data known in the database-table id tuple at a given time and return a page of specific size, using
     * Instant to better abstract time concept (JDK 8) from SQL. We use the "mariadb" user for this.
     *
     * @param containerId The container-database id pair.
     * @param databaseId  The container-database id pair.
     * @param tableId     The table id.
     * @param timestamp   The given time.
     * @param page        The page.
     * @param size        The page size.
     * @param sortBy      The column after which should be sorted.
     * @param sortDesc    The direction it should be sorted, if true the column is sorted Z to A, if false otherwise.
     * @return The select all data result
     * @throws ContainerNotFoundException  The container was not found in the metadata database.
     * @throws TableNotFoundException      The table was not found in the metadata database.
     * @throws TableMalformedException     The table columns are messed up what we got from the metadata database.
     * @throws DatabaseNotFoundException   The database was not found in the remote database.
     * @throws ImageNotSupportedException  The image is not supported.
     * @throws DatabaseConnectionException The connection to the remote database was unsuccessful.
     */
    QueryResultDto findAll(Long containerId, Long databaseId, Long tableId, Instant timestamp,
                           Long page, Long size, String sortBy, Boolean sortDesc) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, TableMalformedException, PaginationException,
            ContainerNotFoundException;

    /**
     * Count the total tuples for a given table id within a container-database id tuple at a given time.
     *
     * @param containerId The container-database id tuple.
     * @param databaseId  The container-database id tuple.
     * @param tableId     The container-database id tuple.
     * @param timestamp   The time.
     * @return The number of records, if successful
     * @throws ContainerNotFoundException The container was not found in the metadata database.
     * @throws DatabaseNotFoundException  The database was not found in the remote database.
     * @throws TableNotFoundException     The table was not found in the metadata database.
     * @throws TableMalformedException    The table columns are messed up what we got from the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     */
    BigInteger count(Long containerId, Long databaseId, Long tableId, Instant timestamp)
            throws ContainerNotFoundException, DatabaseNotFoundException, TableNotFoundException,
            TableMalformedException, ImageNotSupportedException;

    /**
     * Insert data from AMQP client into a table of a table-database id tuple, we need the "root" role for this as the
     * default "mariadb" user is configured to only be allowed to execute "SELECT" statements.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param data       The data.
     * @return The number of tuples affected.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     * @throws DatabaseNotFoundException  The database is not found in the metadata database.
     * @throws TableNotFoundException     The table is not found in the metadata database.
     */
    Integer insert(Long containerId, Long databaseId, Long tableId, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException, ContainerNotFoundException;

    /**
     * Insert data from a csv into a table of a table-database id tuple, we need the "root" role for this as the
     * default "mariadb" user is configured to only be allowed to execute "SELECT statements.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param data       The data path.
     * @return The number of tuples affected.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     * @throws DatabaseNotFoundException  The database is not found in the metadata database.
     * @throws TableNotFoundException     The table is not found in the metadata database.
     */
    Integer insert(Long containerId, Long databaseId, Long tableId, ImportDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException, ContainerNotFoundException, FileStorageException;
}
