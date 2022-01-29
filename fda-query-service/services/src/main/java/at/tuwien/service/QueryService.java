package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public interface QueryService {

    /**
     * Executes an arbitrary query on the database container. We allow the user to only view the data, therefore the
     * default "mariadb" user is allowed read-only access "SELECT".
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param query      The query.
     * @return The result.
     * @throws TableNotFoundException
     * @throws QueryStoreException
     * @throws QueryMalformedException
     * @throws DatabaseNotFoundException
     * @throws ImageNotSupportedException
     */
    QueryResultDto execute(Long databaseId, Long tableId, ExecuteStatementDto query) throws TableNotFoundException,
            QueryStoreException, QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException;

    /**
     * Select all data known in the database-table id tuple at a given time and return a page of specific size, using
     * Instant to better abstract time concept (JDK 8) from SQL. We use the "mariadb" user for this.
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
    QueryResultDto findAll(@NonNull Long databaseId, @NonNull Long tableId, Instant timestamp,
                           Long page, Long size) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, TableMalformedException, PaginationException;

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
    Integer insert(Long databaseId, Long tableId, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException;

    /**
     * Insert data from a csv into a table of a table-database id tuple, we need the "root" role for this as the
     * default "mariadb" user is configured to only be allowed to execute "SELECT statements.
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param path       The data path.
     * @return The number of tuples affected.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     * @throws DatabaseNotFoundException  The database is not found in the metadata database.
     * @throws TableNotFoundException     The table is not found in the metadata database.
     */
    Integer insert(Long databaseId, Long tableId, String path) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException;
}
