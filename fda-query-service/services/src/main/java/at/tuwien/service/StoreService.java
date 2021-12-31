package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.QueryStoreException;

import java.util.List;

public interface StoreService {

    /**
     * Finds all queries in the query store of the given database id and query id.
     *
     * @param databaseId The database id.
     * @return The list of queries.
     * @throws ImageNotSupportedException The image is not supported
     * @throws DatabaseNotFoundException  The database was not found in the metadata database
     * @throws QueryStoreException        The query store produced an invalid result
     */
    List<QueryDto> findAll(Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException;

    /**
     * Finds a query in the query store of the given database id and query id.
     *
     * @param databaseId The database id.
     * @param queryId    The query id.
     * @return The query.
     * @throws ImageNotSupportedException The image is not supported
     * @throws DatabaseNotFoundException  The database was not found in the metadata database
     * @throws QueryStoreException        The query store produced an invalid result
     * @throws QueryNotFoundException     The query store did not return a query
     */
    QueryDto findOne(Long databaseId, Long queryId) throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException, QueryNotFoundException;

    /**
     * Creates the query store on the remote container for a given database id
     *
     * @param databaseId The database id.
     * @throws ImageNotSupportedException The image is not supported
     * @throws DatabaseNotFoundException  The database was not found in the metadata database
     */
    void create(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException;

    /**
     * Creates the query store on the remote container for a given database id
     *
     * @param databaseId The database id.
     * @throws ImageNotSupportedException The image is not supported
     * @throws DatabaseNotFoundException  The database was not found in the metadata database
     */
    void delete(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException;

    QueryDto insert(Long databaseId, QueryResultDto query, ExecuteQueryDto metadata) throws QueryStoreException,
            DatabaseNotFoundException, ImageNotSupportedException;
}
