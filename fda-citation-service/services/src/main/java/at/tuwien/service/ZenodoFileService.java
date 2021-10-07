package at.tuwien.service;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.query.File;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ZenodoMapper;
import at.tuwien.repository.jpa.FileRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class ZenodoFileService implements FileService {

    private final static String FILE_CSV_LOCATION = "/tmp/data.csv";

    private final RestTemplate zenodoTemplate;
    private final RestTemplate queryTemplate;
    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;
    private final FileRepository fileRepository;
    private final TableRepository tableRepository;
    private final QueryRepository queryRepository;

    @Autowired
    public ZenodoFileService(@Qualifier("zenodoTemplate") RestTemplate zenodoTemplate,
                             @Qualifier("queryTemplate") RestTemplate queryTemplate,
                             ZenodoConfig zenodoConfig, ZenodoMapper zenodoMapper,
                             FileRepository fileRepository, TableRepository tableRepository,
                             QueryRepository queryRepository) {
        this.zenodoTemplate = zenodoTemplate;
        this.queryTemplate = queryTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.fileRepository = fileRepository;
        this.tableRepository = tableRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    public File createResource(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException {
        final Query query = getQuery(queryId);
        final ResponseEntity<FileDto> response;
        try {
            response = zenodoTemplate.postForEntity("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
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
        log.info("Created file with id {}", response.getBody().getId());
        final File file = File.builder().build();
        return file;
    }

    @Override
    public List<File> listResources() {
        return fileRepository.findAll();
    }

    @Override
    public File findResource(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException {
        final Query query = getQuery(queryId);
        final ResponseEntity<FileDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
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
    public void deleteResource(Long databaseId, Long tableId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException, QueryNotFoundException {
        final Query query = getQuery(queryId);
        final ResponseEntity<String> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
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
        log.info("Deleted file with id {}", query.getFile().getId());
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
     * @param tableId    The database-table id pair
     * @param queryId    The query id
     * @return The create multipart file
     */
    private MultipartFile getDataset(Long databaseId, Long tableId, Long queryId) throws QueryNotFoundException,
            RemoteDatabaseException, TableServiceException {
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
        return writeFile(response.getBody());
    }

    /**
     * Create a temporary data set file and return a multipart file
     *
     * @param data the data set
     * @return The multipart file
     */
    private MultipartFile writeFile(QueryResultDto data) throws TableServiceException {
        final String[] headers = data.getResult()
                .get(0)
                .keySet()
                .toArray(new String[0]);
        ICsvBeanWriter writer = null;
        try {
            writer = new CsvBeanWriter(new FileWriter(FILE_CSV_LOCATION), CsvPreference.STANDARD_PREFERENCE);
            final CellProcessor[] processors = new CellProcessor[]{};
            writer.writeHeader(headers);
            for (Map<String, Object> row : data.getResult()) {
                writer.write(row, headers, processors);
            }
        } catch (IOException e) {
            throw new TableServiceException("Failed to write csv", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("Could not close the writer");
            }
        }
        final java.io.File file = new java.io.File(FILE_CSV_LOCATION);
        final MultipartFile multipartFile = new CommonsMultipartFile(new DiskFileItem("file", "text/plain", false, file.getName(),
                (int) file.length(), file.getParentFile()));
        log.debug("wrote multipart file {}", multipartFile.getName());
        return multipartFile;
    }

}
