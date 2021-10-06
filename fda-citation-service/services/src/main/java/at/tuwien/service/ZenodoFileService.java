package at.tuwien.service;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.File;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ZenodoMapper;
import at.tuwien.repository.jpa.FileRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ZenodoFileService implements FileService {

    private final RestTemplate apiTemplate;
    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;
    private final FileRepository fileRepository;
    private final TableRepository tableRepository;
    private final QueryRepository queryRepository;

    @Autowired
    public ZenodoFileService(RestTemplate apiTemplate, ZenodoConfig zenodoConfig, ZenodoMapper zenodoMapper,
                             FileRepository fileRepository, TableRepository tableRepository,
                             QueryRepository queryRepository) {
        this.apiTemplate = apiTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.fileRepository = fileRepository;
        this.tableRepository = tableRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    @Transactional
    public File createResource(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException {
        final Query query = getQuery(queryId);
        final ResponseEntity<FileDto> response;
        try {
            response = apiTemplate.postForEntity("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
                    zenodoMapper.resourceToHttpEntity(query.getTitle(), getDataset(databaseId, tableId, queryId)),
                    FileDto.class, query.getDepositId(), zenodoConfig.getApiKey());
        } catch (IOException e) {
            throw new ZenodoApiException("Could not map file to byte array");
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Did not find the resource with this id");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Did not find the resource with this id");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        final File file = File.builder().build();
        return file;
    }

    @Override
    @Transactional
    public List<File> listResources() {
        return fileRepository.findAll();
    }

    @Override
    @Transactional
    public File findResource(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException {
        final Query query = getQuery(queryId);
        final ResponseEntity<FileDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), FileDto.class, query.getDepositId(), query.getFile().getId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ZenodoNotFoundException("Did not find the resoource with this ID");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        final File file = File.builder().build();
        return file;
    }

    @Override
    @Transactional
    public void deleteResource(Long databaseId, Long tableId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException {
        final Query query = getQuery(queryId);
        final ResponseEntity<String> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, query.getDepositId(), query.getFile().getId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ZenodoNotFoundException("Did not find the resource with this ID");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            throw new ZenodoApiException("Failed to delete the resource with this ID");
        }
    }

    /**
     * Retrieve a query from the metadata database
     *
     * @param queryId The query id
     * @return The query
     * @throws QueryNotFoundException Query was not found
     */
    @Transactional
    protected Query getQuery(Long queryId) throws QueryNotFoundException {
        final Optional<Query> query = queryRepository.findById(queryId);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query was not found in metadata database");
        }
        return query.get();
    }

    /**
     * Wrapper function to throw error when table with id was not found
     *
     * @param databaseId The database id
     * @param tableId    The table id
     * @return The table
     * @throws MetadataDatabaseNotFoundException The database was not found in the metadata database
     */
    @Transactional
    protected Table getTable(Long databaseId, Long tableId) throws MetadataDatabaseNotFoundException {
        final Database database = Database.builder()
                .id(databaseId)
                .build();
        final Optional<Table> table = tableRepository.findByDatabaseAndId(database, tableId);
        if (table.isEmpty()) {
            throw new MetadataDatabaseNotFoundException("Failed to find table with this id");
        }
        return table.get();
    }

    /**
     * Wrapper to add headers to all non-file upload requests
     *
     * @param body The request data
     * @return The request with headers
     */
    private HttpEntity<Object> addHeaders(Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * Retrieve a result set from the Table Service and create a multipart file from it
     *
     * @param databaseId The database-table id pair
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @return The create dmultipart file
     */
    private MultipartFile getDataset(Long databaseId, Long tableId, Long queryId) {
        // TODO
        return null;
    }

}
