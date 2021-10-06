package at.tuwien.service;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.DepositDto;
import at.tuwien.api.database.deposit.DepositTzDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ZenodoMapper;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ZenodoMetadataService implements MetadataService {

    private final RestTemplate apiTemplate;
    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;
    private final TableRepository tableRepository;
    private final QueryRepository queryRepository;

    @Autowired
    public ZenodoMetadataService(RestTemplate apiTemplate, ZenodoConfig zenodoConfig, ZenodoMapper zenodoMapper,
                                 TableRepository tableRepository, QueryRepository queryRepository) {
        this.apiTemplate = apiTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.tableRepository = tableRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    @Transactional
    public List<Query> listCitations(Long databaseId, Long tableId) {
        return queryRepository.findAll();
    }

    @Override
    @Transactional
    public Query storeCitation(Long databaseId, Long tableId) throws ZenodoAuthenticationException,
            ZenodoApiException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        final Table table = getTable(databaseId, tableId);
        final ResponseEntity<DepositTzDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions?access_token={token}", HttpMethod.POST,
                    addHeaders("{}"), DepositTzDto.class, zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        }
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoApiException("Failed to store citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return queryRepository.save(zenodoMapper.depositTzDtoToQuery(response.getBody()));
    }

    @Override
    @Transactional
    public Query updateCitation(Long databaseId, Long tableId, Long queryId,
                                DepositChangeRequestDto data)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException {
        final Optional<Query> metaQuery = queryRepository.findById(queryId);
        if (metaQuery.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<DepositDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.PUT, addHeaders(data), DepositDto.class, metaQuery.get().getDepositId(),
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
        final Query query = Query.builder().build();
        return query;
    }

    @Override
    @Transactional
    public Query findCitation(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            QueryNotFoundException, ZenodoUnavailableException {
        final Optional<Query> metaQuery = queryRepository.findById(queryId);
        if (metaQuery.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<DepositDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), DepositDto.class, metaQuery.get().getDepositId(),
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
        final Query query = Query.builder().build();
        return query;
    }

    @Override
    @Transactional
    public void deleteCitation(Long databaseId, Long tableId, Long queryId) throws ZenodoAuthenticationException, ZenodoApiException,
            QueryNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException {
        final Optional<Query> query = queryRepository.findById(queryId);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<String> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, query.get().getDepositId(),
                    zenodoConfig.getApiKey());
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
    public Query publishCitation(Long databaseId, Long tableId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, QueryNotFoundException,
            ZenodoUnavailableException, ZenodoNotFoundException {
        final Optional<Query> metaQuery = queryRepository.findById(queryId);
        if (metaQuery.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<DepositDto> response;
        try {
            response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}/actions/publish?access_token={token}",
                    HttpMethod.POST, addHeaders(null), DepositDto.class, metaQuery.get().getDepositId(),
                    zenodoConfig.getApiKey());
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
        final Query query = Query.builder().build();
        return query;
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
