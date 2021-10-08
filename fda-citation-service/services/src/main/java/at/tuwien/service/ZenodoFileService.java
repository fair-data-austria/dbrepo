package at.tuwien.service;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.File;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.FileMapper;
import at.tuwien.mapper.ZenodoMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.FileRepository;
import at.tuwien.repository.jpa.QueryRepository;
import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class ZenodoFileService implements FileService {

    private final static String FILE_CSV_NAME = "data.csv";
    private final static String FILE_CSV_LOCATION = "/tmp/" + FILE_CSV_NAME;
    private final static Boolean FILE_CSV_QUOTES = false;

    private final FileMapper fileMapper;
    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;
    private final RestTemplate queryTemplate;
    private final RestTemplate zenodoTemplate;
    private final FileRepository fileRepository;
    private final QueryRepository queryRepository;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public ZenodoFileService(FileMapper fileMapper, RestTemplate zenodoTemplate, RestTemplate queryTemplate,
                             ZenodoConfig zenodoConfig, ZenodoMapper zenodoMapper, FileRepository fileRepository,
                             QueryRepository queryRepository, DatabaseRepository databaseRepository) {
        this.fileMapper = fileMapper;
        this.zenodoTemplate = zenodoTemplate;
        this.queryTemplate = queryTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.fileRepository = fileRepository;
        this.queryRepository = queryRepository;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public File createResource(Long databaseId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException,
            ZenodoFileException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Query query = getQuery(queryId);
        final ResponseEntity<FileDto> response;
        try {
            response = zenodoTemplate.postForEntity("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
                    zenodoMapper.resourceToHttpEntity(query.getTitle(), getDataset(databaseId, queryId)),
                    FileDto.class, query.getDepositId(), zenodoConfig.getApiKey());
        } catch (IOException e) {
            throw new ZenodoApiException("Could not map file to byte array");
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Remote server is not accessible", e);
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
        log.info("Created file with id {}", response.getBody().getId());
        return fileMapper.fileDtoToFile(response.getBody());
    }

    @Override
    public List<File> listResources() {
        return fileRepository.findAll();
    }

    @Override
    public File findResource(Long databaseId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Query query = getQuery(queryId);
        if (query.getFiles().size() != 1) {
            log.error("Currently we only support one file");
            throw new QueryNotFoundException("Currently we only support one file");
        }
        final ResponseEntity<FileDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), FileDto.class, query.getDepositId(), query.getFiles().get(0).getId(),
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
        return fileMapper.fileDtoToFile(response.getBody());
    }

    @Override
    public void deleteResource(Long databaseId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Query query = getQuery(queryId);
        if (query.getFiles().size() != 1) {
            log.error("Currently we only support one file");
            throw new QueryNotFoundException("Currently we only support one file");
        }
        final ResponseEntity<String> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, query.getDepositId(), query.getFiles().get(0).getId(),
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
        log.info("Deleted file with id {}", query.getFiles().get(0).getId());
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
     * @param queryId    The query id
     * @return The create multipart file
     */
    private MultipartFile getDataset(Long databaseId, Long queryId) throws QueryNotFoundException,
            RemoteDatabaseException, TableServiceException, ZenodoFileException {
        final ResponseEntity<QueryResultDto> response;
        try {
            response = queryTemplate.exchange("/api/database/{databaseId}/query/{queryId}/data", HttpMethod.GET,
                    null, QueryResultDto.class, databaseId, queryId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new QueryNotFoundException("Failed to find the database on the remote server", e);
        } catch (HttpClientErrorException.MethodNotAllowed e) {
            throw new RemoteDatabaseException("Failed to connect with the remote database", e);
        }
        if (response.getBody() == null) {
            throw new RemoteDatabaseException("Response body is null");
        }
        log.trace("retrieved result set from table service {}", response.getBody().getResult());
        final MultipartFile file = writeFile(response.getBody());
        log.trace("mapped to file {}", file);
        return file;
    }

    /**
     * Find database by id in the metadata database
     *
     * @param id The id
     * @return The database
     * @throws MetadataDatabaseNotFoundException When not found
     */
    private Database find(Long id) throws MetadataDatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            throw new MetadataDatabaseNotFoundException("Database not found in the metadata database");
        }
        return database.get();
    }

    /**
     * Create a temporary data set file and return a multipart file
     *
     * @param data the data set
     * @return The multipart file
     */
    private MultipartFile writeFile(QueryResultDto data) throws TableServiceException, ZenodoFileException {
        final String[] headers = data.getResult()
                .get(0)
                .keySet()
                .toArray(new String[0]);
        final CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(FILE_CSV_LOCATION));
            writer.writeNext(headers, FILE_CSV_QUOTES);
            for (Map<String, Object> cells : data.getResult()) {
                final String[] row = cells.values()
                        .stream()
                        .map(String::valueOf)
                        .toArray(String[]::new);
                writer.writeNext(row, FILE_CSV_QUOTES);
            }
            writer.close();
        } catch (IOException e) {
            log.error("Failed to write csv");
            throw new TableServiceException("Failed to write csv", e);
        }
        final MultipartFile multipartFile;
        try {
            multipartFile = new MockMultipartFile(FILE_CSV_NAME, new FileInputStream(FILE_CSV_LOCATION));
        } catch (IOException e) {
            log.error("Failed to read generated csv from the query");
            throw new ZenodoFileException("Failed to read generated csv from the query", e);
        }
        log.debug("wrote multipart file {}", multipartFile.getName());
        return multipartFile;
    }

}
