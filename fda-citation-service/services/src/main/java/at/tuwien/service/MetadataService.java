package at.tuwien.service;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MetadataService {

    /**
     * List all deposits (e.g. datasets) available
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @return The deposists
     */
    List<Query> listCitations(Long databaseId, Long tableId);

    /**
     * Create a new deposit
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @return The created deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     * @throws ZenodoUnavailableException    The remote server is not available
     */
    Query storeCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException,
            ZenodoApiException, MetadataDatabaseNotFoundException, ZenodoUnavailableException;

    /**
     * Update a deposit with new metadata for a given id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @param data       The new metadata
     * @return The updated deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     * @throws ZenodoNotFoundException       The deposit id was not found on the remote server
     * @throws ZenodoUnavailableException    The remote server is not available
     */
    Query updateCitation(Long databaseId, Long tableId, Long queryId,
                            DepositChangeRequestDto data) throws ZenodoAuthenticationException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoUnavailableException, QueryNotFoundException;

    /**
     * Find a deposit by database-table id pair
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @return The deposit
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws ZenodoUnavailableException        The remote server is not available
     */
    Query findCitation(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException, QueryNotFoundException;

    /**
     * Delete a deposit from a given id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws ZenodoUnavailableException        The remote server is not available
     * @throws ZenodoNotFoundException           The deposit was not found on the remote server
     */
    void deleteCitation(Long databaseId, Long tableId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoApiException, MetadataDatabaseNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException,
            QueryNotFoundException;

    /**
     * Publishes a deposit with database-table id pair
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @return The deposit
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     * @throws ZenodoUnavailableException        The remote server is not available
     * @throws ZenodoNotFoundException           The deposit was not found on the remote server
     */
    Query publishCitation(Long databaseId, Long tableId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoApiException, MetadataDatabaseNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException,
            QueryNotFoundException;
}
