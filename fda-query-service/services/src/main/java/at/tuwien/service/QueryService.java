package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
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
    @Transactional
    QueryResultDto execute(Long databaseId, Long tableId, QueryDto query) throws TableNotFoundException,
            QueryStoreException, QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException;

    /**
     * Executes a query on a given database id and table id on a read-only session
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param query      The query.
     * @return The result.
     * @throws DatabaseNotFoundException
     * @throws ImageNotSupportedException
     * @throws QueryMalformedException
     * @throws TableNotFoundException
     * @throws QueryStoreException
     */
    QueryResultDto execute(Long databaseId, Long tableId, ExecuteQueryDto query) throws DatabaseNotFoundException,
            ImageNotSupportedException, QueryMalformedException, TableNotFoundException, QueryStoreException;

    /**
     * Select all data known in the database-table id tuple at a given time and return a page of specific size, using
     * Instant to better abstract time concept (JDK 8) from SQL
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
     * Insert data from AMQP client into a table of a table-database id tuple
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param data       The data.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     */
    void insert(Long databaseId, Long tableId, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException;
}
