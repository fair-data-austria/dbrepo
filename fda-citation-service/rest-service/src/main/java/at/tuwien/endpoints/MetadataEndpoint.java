package at.tuwien.endpoints;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.MetadataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/metadata")
public class MetadataEndpoint {

    private final QueryMapper queryMapper;
    private final MetadataService metadataService;

    @Autowired
    public MetadataEndpoint(QueryMapper queryMapper, MetadataService metadataService) {
        this.queryMapper = queryMapper;
        this.metadataService = metadataService;
    }

    @GetMapping
    public List<QueryDto> findAll(@Valid @RequestParam("id") Long databaseId,
                                  @Valid @RequestParam("tableId") Long tableId) {
        return metadataService.listCitations(databaseId, tableId)
                .stream()
                .map(queryMapper::queryToQueryDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public QueryDto create(@Valid @RequestParam("id") Long databaseId,
                           @Valid @RequestParam("tableId") Long tableId) throws ZenodoApiException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        return queryMapper.queryToQueryDto(metadataService.storeCitation(databaseId, tableId));
    }

    @GetMapping("/{queryId}")
    public QueryDto find(@Valid @RequestParam("id") Long databaseId,
                         @Valid @RequestParam("tableId") Long tableId,
                         @Valid @RequestParam("queryId") Long queryId)
            throws MetadataDatabaseNotFoundException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.findCitation(databaseId, tableId, queryId));
    }

    @PutMapping("/{queryId}")
    public QueryDto update(@Valid @RequestParam("id") Long databaseId,
                           @Valid @RequestParam("tableId") Long tableId,
                           @Valid @RequestParam("queryId") Long queryId,
                           @Valid @RequestBody DepositChangeRequestDto data)
            throws ZenodoApiException, ZenodoNotFoundException, ZenodoAuthenticationException,
            ZenodoUnavailableException, QueryNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.updateCitation(databaseId, tableId, queryId, data));
    }

    @DeleteMapping("/{queryId}")
    public void delete(@Valid @RequestParam("id") Long databaseId,
                       @Valid @RequestParam("tableId") Long tableId,
                       @Valid @RequestParam("queryId") Long queryId) throws MetadataDatabaseNotFoundException,
            ZenodoApiException, ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException {
        metadataService.deleteCitation(databaseId, tableId, queryId);
    }

    @PostMapping("/{queryId}/publish")
    public QueryDto publish(@Valid @RequestParam("id") Long databaseId,
                            @Valid @RequestParam("tableId") Long tableId,
                            @Valid @RequestParam("queryId") Long queryId) throws ZenodoApiException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            ZenodoNotFoundException, QueryNotFoundException {
        return queryMapper.queryToQueryDto(metadataService.publishCitation(databaseId, tableId, queryId));
    }
}
