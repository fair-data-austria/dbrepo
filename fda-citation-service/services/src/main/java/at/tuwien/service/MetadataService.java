package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.exception.MetadataDatabaseNotFoundException;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
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
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    List<DepositResponseDto> listCitations(Long databaseId, Long tableId) throws ZenodoAuthenticationException, ZenodoApiException, MetadataDatabaseNotFoundException;

    /**
     * Create a new deposit
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @return The created deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    DepositChangeResponseDto storeCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException, ZenodoApiException, MetadataDatabaseNotFoundException;

    /**
     * Update a deposit with new metadata for a given id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param data       The new metadata
     * @return The updated deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     * @throws ZenodoNotFoundException       The deposit id was not found on the remote server
     */
    DepositChangeResponseDto updateCitation(Long databaseId, Long tableId, DepositChangeRequestDto data) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoNotFoundException, MetadataDatabaseNotFoundException;

    /**
     * Find a deposit by database-table id pair
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @return The deposit
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws MetadataDatabaseNotFoundException The deposit id was not found in the metadata database
     */
    DepositResponseDto findCitation(Long databaseId, Long tableId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            MetadataDatabaseNotFoundException;

    /**
     * Delete a deposit from a given id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    void deleteCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException, ZenodoApiException, MetadataDatabaseNotFoundException;
}
