package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public interface FileService {

    /**
     * Upload a new file to a remote server for a given database-table id pair and metadata
     *
     * @param databaseId The database-table id paid
     * @param tableId    The database-table id pair
     * @param data       The metadata
     * @param resource   The file
     * @return The new file
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws ZenodoFileTooLargeException       The file exceeds the capabilities
     * @throws MetadataDatabaseNotFoundException The deposit was not found on the metadata database
     */
    FileResponseDto createResource(Long databaseId, Long tableId, FileUploadDto data, MultipartFile resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoFileTooLargeException, MetadataDatabaseNotFoundException, ZenodoUnavailableException;

    /**
     * List all files known to a deposit number (through the database-table id pair)
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @return The list of files
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoApiException                Something other went wrong
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws MetadataDatabaseNotFoundException The deposit was not found on the metadata database
     */
    List<FileResponseDto> listResources(Long databaseId, Long tableId) throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException;

    /**
     * Find a file for a deposit (through the database-table id pair) by id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param fileId     The file id
     * @return The file
     * @throws MetadataDatabaseNotFoundException The deposit was not found on the metadata database
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws ZenodoApiException                Something other went wrong
     */
    FileResponseDto findResource(Long databaseId, Long tableId, String fileId) throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException;

    /**
     * Delete a file based on the database-table id pair by id
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param fileId     The file id
     * @throws MetadataDatabaseNotFoundException The deposit was not found on the metadata database
     * @throws ZenodoAuthenticationException     Token invalid
     * @throws ZenodoNotFoundException           The deposit id was not found on the remote server
     * @throws ZenodoApiException                Something other went wrong
     */
    void deleteResource(Long databaseId, Long tableId, String fileId)
            throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException;
}
