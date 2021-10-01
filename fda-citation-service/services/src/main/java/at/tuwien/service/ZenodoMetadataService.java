package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ZenodoMetadataService implements MetadataService {

    private final RestTemplate apiTemplate;
    private final ZenodoConfig zenodoConfig;
    private final TableRepository tableRepository;

    @Autowired
    public ZenodoMetadataService(RestTemplate apiTemplate, ZenodoConfig zenodoConfig, TableRepository tableRepository) {
        this.apiTemplate = apiTemplate;
        this.zenodoConfig = zenodoConfig;
        this.tableRepository = tableRepository;
    }

    @Override
    @Transactional
    public List<DepositResponseDto> listCitations(Long databaseId, Long tableId) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoUnavailableException {
        final ResponseEntity<DepositResponseDto[]> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions?access_token={token}",
                    HttpMethod.GET, addHeaders(null), DepositResponseDto[].class, zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return Arrays.asList(response.getBody());
    }

    @Override
    @Transactional
    public DepositChangeResponseDto storeCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException,
            ZenodoApiException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<DepositChangeResponseDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions?access_token={token}", HttpMethod.POST,
                    addHeaders("{}"), DepositChangeResponseDto.class, zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) { // 400
            throw new ZenodoApiException("Failed to store citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        table.setDepositId(response.getBody().getId());
        tableRepository.save(table);
        return response.getBody();
    }

    @Override
    @Transactional
    public DepositChangeResponseDto updateCitation(Long databaseId, Long tableId, DepositChangeRequestDto data)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<DepositChangeResponseDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.PUT, addHeaders(data), DepositChangeResponseDto.class, table.getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Could not get the citation.");
        }
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ZenodoAuthenticationException("Could not update the citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return response.getBody();
    }

    @Override
    @Transactional
    public DepositResponseDto findCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<DepositResponseDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), DepositResponseDto.class, table.getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Could not get the citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return response.getBody();
    }

    @Override
    @Transactional
    public void deleteCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException, ZenodoApiException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<String> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, table.getDepositId(), zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new ZenodoApiException("Could not delete the deposit");
        }
    }

    @Override
    @Transactional
    public DepositChangeResponseDto publishCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException, ZenodoApiException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<DepositChangeResponseDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/actions/publish?access_token={token}",
                    HttpMethod.POST, addHeaders(null), DepositChangeResponseDto.class, table.getDepositId(), zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (!response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            throw new ZenodoApiException("Could not publish the deposit");
        }
        return response.getBody();
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
