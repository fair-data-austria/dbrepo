package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.query.SaveStatementDto;
import at.tuwien.exception.*;
import at.tuwien.querystore.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    List<Query> findAll(Long containerId, Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, ContainerNotFoundException;

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
    Query findOne(Long containerId, Long databaseId, Long queryId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, QueryNotFoundException, ContainerNotFoundException;

    /**
     * Inserts a query and metadata to the query store of a given database id
     *
     * @param databaseId The database id.
     * @param result     The query.
     * @param metadata   The metadata.
     * @return The stored query on success
     * @throws QueryStoreException        The query store raised some error
     * @throws DatabaseNotFoundException  The database id was not found in the metadata database
     * @throws ImageNotSupportedException The image is not supported
     */
    Query insert(Long containerId, Long databaseId, QueryResultDto result, SaveStatementDto metadata)
            throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException;

    /**
     * Inserts a query and metadata to the query store of a given database id
     *
     * @param databaseId The database id.
     * @param result     The query.
     * @param metadata   The metadata.
     * @return The stored query on success
     * @throws QueryStoreException        The query store raised some error
     * @throws DatabaseNotFoundException  The database id was not found in the metadata database
     * @throws ImageNotSupportedException The image is not supported
     */
    Query insert(Long containerId, Long databaseId, QueryResultDto result, ExecuteStatementDto metadata) throws QueryStoreException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException;

}
