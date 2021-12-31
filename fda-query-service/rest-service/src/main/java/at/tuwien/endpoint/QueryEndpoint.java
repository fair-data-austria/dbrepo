package at.tuwien.endpoint;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.exception.*;
import at.tuwien.service.QueryService;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/database/{id}/query")
public class QueryEndpoint {

    private final QueryService queryService;
    private final StoreService storeService;

    @Autowired
    public QueryEndpoint(QueryService queryService, StoreService storeService) {
        this.queryService = queryService;
        this.storeService = storeService;
    }

    @PutMapping("/table/{tableId}/execute")
    @ApiOperation(value = "executes a query and save the results")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> execute(@PathVariable("id") Long databaseId,
                                                  @PathVariable Long tableId,
                                                  @RequestBody @Valid ExecuteQueryDto data)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException, QueryMalformedException,
            TableNotFoundException {
        final QueryResultDto result = queryService.execute(databaseId, tableId, data);
        final QueryDto query = storeService.insert(databaseId, result, data);
        result.setId(query.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(result);
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
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(storeService.insert(databaseId, null, data));
    }

    @PutMapping("/table/{tableId}/execute/{queryId}")
    @ApiOperation(value = "re-executes a query by given id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Re-Execute a saved query and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> reExecute(@PathVariable Long id,
                                                    @PathVariable Long tableId,
                                                    @PathVariable Long queryId)
            throws QueryStoreException, QueryNotFoundException, DatabaseNotFoundException, ImageNotSupportedException,
            TableNotFoundException, QueryMalformedException {
        final QueryDto query = storeService.findOne(id, queryId);
        final QueryResultDto result = queryService.execute(id, tableId, query);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(result);
    }

}
