package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ZenodoMapper;
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
    private final TableRepository tableRepository;

    @Autowired
    public ZenodoFileService(RestTemplate apiTemplate, ZenodoConfig zenodoConfig, ZenodoMapper zenodoMapper,
                             TableRepository tableRepository) {
        this.apiTemplate = apiTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.tableRepository = tableRepository;
    }

    @Override
    @Transactional
    public FileResponseDto createResource(Long databaseId, Long tableId, FileUploadDto data, MultipartFile resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoFileTooLargeException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        if (resource.getSize() > 50_1000_1000_1000L) {
            throw new ZenodoFileTooLargeException("Only 50GB per file is allowed!");
        }
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<FileResponseDto> response;
        try {
            response = apiTemplate.postForEntity("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
                    zenodoMapper.resourceToHttpEntity(data.getName(), resource), FileResponseDto.class, table.getDepositId(), zenodoConfig.getApiKey());
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
        return response.getBody();
    }

    @Override
    @Transactional
    public List<FileResponseDto> listResources(Long databaseId, Long tableId) throws MetadataDatabaseNotFoundException,
            ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoApiException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<FileResponseDto[]> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
                    HttpMethod.GET, addHeaders(null), FileResponseDto[].class, table.getDepositId(), zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ZenodoNotFoundException("Did not find the resoource with this id");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return Arrays.asList(response.getBody());
    }

    @Override
    @Transactional
    public FileResponseDto findResource(Long databaseId, Long tableId, String fileId)
            throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<FileResponseDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), FileResponseDto.class, table.getDepositId(), fileId,
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
        return response.getBody();
    }

    @Override
    @Transactional
    public void deleteResource(Long databaseId, Long tableId, String fileId)
            throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException,
            ZenodoApiException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<String> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/files/{file_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, table.getDepositId(), fileId,
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
     * Wrapper function to throw error when table with id was not found
     *
     * @param databaseId The database id
     * @param tableId    The table id
     * @return The table
     * @throws MetadataDatabaseNotFoundException The error
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

}
