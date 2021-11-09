package at.tuwien.service;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.DepositDto;
import at.tuwien.api.database.deposit.DepositTzDto;
import at.tuwien.api.database.deposit.metadata.ResourceTypeDto;
import at.tuwien.api.database.deposit.record.RecordDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ZenodoMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ZenodoMetadataService implements MetadataService {

    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;
    private final RestTemplate zenodoTemplate;
    private final DatabaseRepository databaseRepository;
    private final QueryRepository queryRepository;

    @Autowired
    public ZenodoMetadataService(@Qualifier("zenodoTemplate") RestTemplate zenodoTemplate, ZenodoConfig zenodoConfig,
                                 ZenodoMapper zenodoMapper, DatabaseRepository databaseRepository,
                                 QueryRepository queryRepository) {
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
        this.zenodoTemplate = zenodoTemplate;
        this.databaseRepository = databaseRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    @Transactional
    public List<Query> listCitations(Long databaseId) throws MetadataDatabaseNotFoundException {
        return queryRepository.findByDatabase(find(databaseId));
    }

    @Override
    @Transactional
    public Query storeCitation(Long databaseId, Long queryId) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoUnavailableException, MetadataDatabaseNotFoundException, ZenodoNotFoundException {
        final Database database = find(databaseId);
        final ResponseEntity<DepositTzDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions?access_token={token}", HttpMethod.POST,
                    addHeaders("{}"), DepositTzDto.class, zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        }
        responseErrorWrapper(response);
        final Query query = zenodoMapper.depositTzDtoToQuery(response.getBody());
        query.setQdbid(databaseId);
        query.setDatabase(database);
        query.setId(queryId);
        log.info("Created query metadata id {} and doi {}", query.getId(), query.getDoi());
        log.debug("Created query metadata {}", response.getBody());
        return queryRepository.save(query);
    }

    @Override
    @Transactional
    public Query updateCitation(Long databaseId, Long queryId,
                                DepositChangeRequestDto data)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Optional<Query> query = queryRepository.findByDatabaseAndId(database, queryId);
        log.debug("");
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        if (query.get().getDepositId() == null) {
            throw new QueryNotFoundException("Deposit ID is null");
        }
        final ResponseEntity<DepositTzDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.PUT, addHeaders(data), DepositTzDto.class, query.get().getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ZenodoAuthenticationException("Could not update the citation.");
        }
        responseErrorWrapper(response);
        log.info("Updated query metadata id {}", queryId);
        log.debug("Updated query metadata {}", response.getBody());
        return query.get();
    }

    @Override
    @Transactional
    public Query findCitation(Long databaseId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            QueryNotFoundException, ZenodoUnavailableException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Optional<Query> query = queryRepository.findByDatabaseAndId(database, queryId);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<DepositDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), DepositDto.class, query.get().getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        responseErrorWrapper(response);
        log.debug("Found query metadata {}", response.getBody());
        return query.get();
    }

    @Override
    @Transactional
    public RecordDto fetchRemoteRecord(Long depositId)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoUnavailableException {
        final ResponseEntity<RecordDto> response;
        try {
            response = zenodoTemplate.exchange("/api/records/{deposit_id}?access_token={token}",
                    HttpMethod.GET, addHeaders(null), RecordDto.class, depositId, zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        responseErrorWrapper(response);
        log.debug("Found query metadata {}", response.getBody());
        return response.getBody();
    }

    @Override
    @Transactional
    public void deleteCitation(Long databaseId, Long queryId) throws ZenodoAuthenticationException, ZenodoApiException,
            QueryNotFoundException, ZenodoUnavailableException, ZenodoNotFoundException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Optional<Query> query = queryRepository.findByDatabaseAndId(database, queryId);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<String> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                    HttpMethod.DELETE, addHeaders(null), String.class, query.get().getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        responseErrorWrapper(response);
        log.info("Deleted query metadata id {}", queryId);
        log.debug("Deleted query metadata {}", response.getBody());
    }

    @Override
    @Transactional
    public Query publishCitation(Long databaseId, Long queryId)
            throws ZenodoAuthenticationException, ZenodoApiException, QueryNotFoundException,
            ZenodoUnavailableException, ZenodoNotFoundException, MetadataDatabaseNotFoundException {
        final Database database = find(databaseId);
        final Optional<Query> query = queryRepository.findByDatabaseAndId(database, queryId);
        if (query.isEmpty()) {
            throw new QueryNotFoundException("Query not found in query store");
        }
        final ResponseEntity<DepositDto> response;
        try {
            response = zenodoTemplate.exchange("/api/deposit/depositions/{deposit_id}/actions/publish?access_token={token}",
                    HttpMethod.POST, addHeaders(null), DepositDto.class, query.get().getDepositId(),
                    zenodoConfig.getApiKey());
        } catch (ResourceAccessException e) {
            throw new ZenodoUnavailableException("Zenodo host is not reachable from the service network", e);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest e) {
            throw new ZenodoNotFoundException("Could not get the citation.", e);
        }
        responseErrorWrapper(response);
        log.info("Published query metadata id {} and doi {}", queryId, query.get().getDoi());
        log.debug("Published query metadata {}", response.getBody());
        return query.get();
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

    private void responseErrorWrapper(ResponseEntity<?> response) throws ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoApiException {
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Could not get the citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
    }
}
