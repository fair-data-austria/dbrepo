package at.tuwien.endpoints;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.MetadataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/cite/metadata")
public class MetadataEndpoint {

    private final QueryMapper queryMapper;
    private final MetadataService metadataService;

    @Autowired
    public MetadataEndpoint(QueryMapper queryMapper, MetadataService metadataService) {
        this.queryMapper = queryMapper;
        this.metadataService = metadataService;
    }

    @GetMapping
    @Transactional
    public List<QueryDto> findAll(@Valid @PathVariable("id") Long databaseId) throws MetadataDatabaseNotFoundException {
        return metadataService.listCitations(databaseId)
                .stream()
                .map(queryMapper::queryToQueryDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{queryId}")
    @Transactional
    public QueryDto create(@Valid @PathVariable("id") Long databaseId,
                           @Valid @PathVariable("queryId") Long queryId) throws RemoteApiException,
            RemoteAuthenticationException, MetadataDatabaseNotFoundException, RemoteUnavailableException,
            RemoteNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.storeCitation(databaseId, queryId));
    }

    @GetMapping("/{queryId}")
    @Transactional
    public QueryDto find(@Valid @PathVariable("id") Long databaseId,
                         @Valid @RequestParam("queryId") Long queryId)
            throws MetadataDatabaseNotFoundException, RemoteApiException, RemoteNotFoundException,
            RemoteAuthenticationException, RemoteUnavailableException, QueryNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.findCitation(databaseId, queryId));
    }

    @PutMapping("/{queryId}")
    @Transactional
    public QueryDto update(@Valid @PathVariable("id") Long databaseId,
                           @Valid @PathVariable("queryId") Long queryId,
                           @Valid @RequestBody DepositChangeRequestDto data)
            throws RemoteApiException, RemoteNotFoundException, RemoteAuthenticationException,
            RemoteUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.updateCitation(databaseId, queryId, data));
    }

    @DeleteMapping("/{queryId}")
    @Transactional
    public void delete(@Valid @PathVariable("id") Long databaseId,
                       @Valid @PathVariable("queryId") Long queryId) throws MetadataDatabaseNotFoundException,
            RemoteApiException, RemoteAuthenticationException, RemoteNotFoundException, RemoteUnavailableException,
            QueryNotFoundException {
        metadataService.deleteCitation(databaseId, queryId);
    }

    @PostMapping("/{queryId}/publish")
    @Transactional
    public QueryDto publish(@Valid @PathVariable("id") Long databaseId,
                            @Valid @PathVariable("queryId") Long queryId) throws RemoteApiException,
            RemoteAuthenticationException, MetadataDatabaseNotFoundException, RemoteUnavailableException,
            RemoteNotFoundException, QueryNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.publishCitation(databaseId, queryId));
    }
}
