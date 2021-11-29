package at.tuwien.service;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.record.RecordDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public interface MetadataService {

    /**
     * List all deposits (e.g. datasets) available
     *
     * @param databaseId The database-table id pair
     * @return The deposists
     */
    List<Query> listCitations(Long databaseId) throws MetadataDatabaseNotFoundException;

    /**
     * Create a new deposit
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @return The created deposit
     * @throws RemoteAuthenticationException Token invalid
     * @throws RemoteApiException            Something other went wrong
     * @throws RemoteUnavailableException    The remote server is not available
     */
    Query storeCitation(Long databaseId, Long queryId) throws RemoteAuthenticationException,
            RemoteApiException, MetadataDatabaseNotFoundException, RemoteUnavailableException, RemoteNotFoundException;

    /**
     * Update a deposit with new metadata for a given id
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @param data       The new metadata
     * @return The updated deposit
     * @throws RemoteAuthenticationException Token invalid
     * @throws RemoteApiException            Something other went wrong
     * @throws RemoteNotFoundException       The deposit id was not found on the remote server
     * @throws RemoteUnavailableException    The remote server is not available
     */
    Query updateCitation(Long databaseId, Long queryId,
                         DepositChangeRequestDto data) throws RemoteAuthenticationException, RemoteApiException,
            RemoteNotFoundException, RemoteUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException;

    /**
     * Find a deposit by database-table id pair
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @return The deposit
     * @throws RemoteAuthenticationException     Token invalid
     * @throws RemoteApiException                Something other went wrong
     * @throws RemoteNotFoundException           The deposit id was not found on the remote server
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws RemoteUnavailableException        The remote server is not available
     */
    Query findCitation(Long databaseId, Long queryId)
            throws RemoteAuthenticationException, RemoteApiException, RemoteNotFoundException,
            MetadataDatabaseNotFoundException, RemoteUnavailableException, QueryNotFoundException;

    /**
     * Fetches a record by depositId
     *
     * @param depositId The depositId (e.g. 956194)
     * @return The record
     * @throws RemoteAuthenticationException
     * @throws RemoteApiException
     * @throws RemoteNotFoundException
     * @throws QueryNotFoundException
     * @throws RemoteUnavailableException
     * @throws MetadataDatabaseNotFoundException
     */
    @Transactional
    RecordDto fetchRemoteRecord(Long depositId)
            throws RemoteAuthenticationException, RemoteApiException, RemoteNotFoundException,
            QueryNotFoundException, RemoteUnavailableException, MetadataDatabaseNotFoundException;

    /**
     * Delete a deposit from a given id
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @throws RemoteAuthenticationException     Token invalid
     * @throws RemoteApiException                Something other went wrong
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws RemoteUnavailableException        The remote server is not available
     * @throws RemoteNotFoundException           The deposit was not found on the remote server
     */
    void deleteCitation(Long databaseId, Long queryId) throws RemoteAuthenticationException,
            RemoteApiException, MetadataDatabaseNotFoundException, RemoteUnavailableException, RemoteNotFoundException,
            QueryNotFoundException;

    /**
     * Publishes a deposit with database-table id pair
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @return The deposit
     * @throws RemoteAuthenticationException     Token invalid
     * @throws RemoteApiException                Something other went wrong
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws RemoteUnavailableException        The remote server is not available
     * @throws RemoteNotFoundException           The deposit was not found on the remote server
     */
    Query publishCitation(Long databaseId, Long queryId) throws RemoteAuthenticationException,
            RemoteApiException, MetadataDatabaseNotFoundException, RemoteUnavailableException, RemoteNotFoundException,
            QueryNotFoundException;
}
