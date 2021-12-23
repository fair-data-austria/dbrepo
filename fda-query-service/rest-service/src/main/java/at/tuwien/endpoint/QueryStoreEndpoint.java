package at.tuwien.endpoint;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryStoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/database/{id}/store")
public class QueryStoreEndpoint {

    private QueryStoreService querystoreService;
    private QueryMapper queryMapper;

    @Autowired
    public QueryStoreEndpoint(QueryStoreService queryService, QueryMapper queryMapper) {
        this.querystoreService = queryService;
        this.queryMapper = queryMapper;
    }

    @PostMapping
    @ApiOperation(value = "Creates the query Store")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created the querystore successfully"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<?> create(@PathVariable Long id) throws ImageNotSupportedException, DatabaseNotFoundException,
            QueryStoreException, DatabaseConnectionException {
        querystoreService.create(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/table/{tableId}/execute")
    @ApiOperation(value = "executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> execute(@PathVariable("id") Long databaseId,
                                                  @PathVariable Long tableId,
                                                  @RequestBody ExecuteQueryDto data)
            throws QueryStoreException, DatabaseConnectionException, QueryMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, TableNotFoundException {
        return ResponseEntity.ok(querystoreService.execute(databaseId, tableId, data));
    }

    @PostMapping("/table/{tableId}/save")
    @ApiOperation(value = "saves a query without execution")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryDto> save(@PathVariable("id") Long databaseId,
                                         @PathVariable Long tableId,
                                         @RequestBody ExecuteQueryDto data)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException,
            DatabaseConnectionException, QueryMalformedException, TableNotFoundException {
        return ResponseEntity.ok(queryMapper.queryToQueryDto(
                querystoreService.saveWithoutExecution(databaseId, tableId, data)));
    }

    @PutMapping("/table/{tableId}/execute/{queryId}")
    @ApiOperation(value = "re-executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Re-Execute a saved query and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> reexecute(@PathVariable Long id,
                                                    @PathVariable Long queryId,
                                                    @RequestParam(name = "page", required = false) Integer page,
                                                    @RequestParam(name = "size", required = false) Integer size)
            throws DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException,
            QueryStoreException, QueryMalformedException {
        return ResponseEntity.ok(querystoreService.reexecute(id, queryId, page, size));
    }

    @GetMapping
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored query."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) throws DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, QueryStoreException {
        final List<Query> queries = querystoreService.findAll(id);
        return ResponseEntity.ok(queries.stream()
                .map(q -> queryMapper.queryToQueryDto(q))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{queryId}")
    @ApiOperation(value = "Find a query", notes = "Find a query")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored queries."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<QueryDto> find(@PathVariable Long id,
                                         @PathVariable Long queryId)
            throws DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException,
            QueryStoreException {
        return ResponseEntity.ok(querystoreService.findOne(id, queryId));
    }
}
