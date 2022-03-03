package at.tuwien.endpoint;

import at.tuwien.api.database.query.*;
import at.tuwien.querystore.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.time.Instant;

@Log4j2
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/query")
public class QueryEndpoint {

    private final QueryMapper queryMapper;
    private final QueryService queryService;
    private final StoreService storeService;

    @Autowired
    public QueryEndpoint(QueryMapper queryMapper, QueryService queryService, StoreService storeService) {
        this.queryMapper = queryMapper;
        this.queryService = queryService;
        this.storeService = storeService;
    }

    @Deprecated
    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "executes a query and save the results")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 400, message = "The payload is malformed."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> execute(@NotNull @PathVariable("id") Long id,
                                                  @NotNull @PathVariable("databaseId") Long databaseId,
                                                  @Valid @RequestBody ExecuteStatementDto data,
                                                  @RequestParam(value = "page", required = false ) Long page, @RequestParam(value = "size", required = false) Long size)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException, QueryMalformedException,
            TableNotFoundException, ContainerNotFoundException, SQLException, JSQLParserException, TableMalformedException {
        /* validation */
        if (data.getStatement() == null || data.getStatement().isBlank()) {
            log.error("Query is empty");
            throw new QueryMalformedException("Invalid query");
        }
        if (data.getTables().size() == 0) {
            log.error("Table list is empty");
            throw new QueryMalformedException("Invalid table");
        }
        log.debug("Data for execution: {}", data);
        final QueryResultDto result = queryService.execute(id, databaseId, data, page, size);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(result);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "saves a query without execution")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryDto> save(@NotNull @PathVariable("id") Long id,
                                         @NotNull @PathVariable("databaseId") Long databaseId,
                                         @Valid @RequestBody SaveStatementDto data)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException,
            ContainerNotFoundException {
        final Query query = storeService.insert(id, databaseId, null, data);
        final QueryDto queryDto = queryMapper.queryToQueryDto(query);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(queryDto);
    }

    @PutMapping("/{queryId}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "re-executes a query by given id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Re-Execute a saved query and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> reExecute(@NotNull @PathVariable("id") Long id,
                                                    @NotNull @PathVariable("databaseId") Long databaseId,
                                                    @NotNull @PathVariable("queryId") Long queryId,
                                                    @RequestParam(value = "page", required = false) Long page, @RequestParam(value = "size", required = false) Long size)
            throws QueryStoreException, QueryNotFoundException, DatabaseNotFoundException, ImageNotSupportedException,
            TableNotFoundException, QueryMalformedException, ContainerNotFoundException, SQLException, JSQLParserException, TableMalformedException {
        final Query query = storeService.findOne(id, databaseId, queryId);
        log.debug(query.toString());
        final QueryResultDto result = queryService.reExecute(id, databaseId, query, page, size);
        result.setId(queryId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(result);
    }

}
