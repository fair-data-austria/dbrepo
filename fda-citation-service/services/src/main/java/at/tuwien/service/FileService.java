package at.tuwien.service;

import at.tuwien.entities.database.query.File;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public interface FileService {

    /**
     * Upload a new file to a remote server for a given database-table id pair and metadata
     *
     * @param databaseId The database-table id paid
     * @param queryId    The query id
     * @return The new file
     * @throws RemoteAuthenticationException Token invalid
     * @throws RemoteApiException            Something other went wrong
     * @throws RemoteNotFoundException       The deposit id was not found on the remote server
     * @throws RemoteUnavailableException    The remote server is not reachable
     * @throws QueryNotFoundException        The deposit was not found on the metadata database
     */
    @Transactional
    File createResource(Long databaseId, Long queryId)
            throws RemoteAuthenticationException, RemoteApiException, RemoteNotFoundException,
            RemoteUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException, RemoteFileException, MetadataDatabaseNotFoundException;

    /**
     * List all files known to a deposit number (through the database-table id pair)
     *
     * @return The list of files
     */
    @Transactional
    List<File> listResources();

    /**
     * Find a file for a deposit (through the database-table id pair) by id
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @return The file
     * @throws QueryNotFoundException        The deposit was not found on the metadata database
     * @throws RemoteAuthenticationException Token invalid
     * @throws RemoteNotFoundException       The deposit id was not found on the remote server
     * @throws RemoteUnavailableException    The remote server is not reachable
     * @throws RemoteApiException            Something other went wrong
     */
    @Transactional
    File findResource(Long databaseId, Long queryId)
            throws RemoteAuthenticationException, RemoteNotFoundException,
            RemoteApiException, RemoteUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException;

    /**
     * Delete a file based on the database-table id pair by id
     *
     * @param databaseId The database-table id pair
     * @param queryId    The query id
     * @throws QueryNotFoundException        The deposit was not found on the metadata database
     * @throws RemoteAuthenticationException Token invalid
     * @throws RemoteNotFoundException       The deposit id was not found on the remote server
     * @throws RemoteUnavailableException    The remote server is not reachable
     * @throws RemoteApiException            Something other went wrong
     */
    @Transactional
    void deleteResource(Long databaseId, Long queryId) throws RemoteAuthenticationException,
            RemoteNotFoundException, RemoteApiException, RemoteUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException;
}
