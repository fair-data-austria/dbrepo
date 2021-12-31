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
    @ApiOperation(value = "executes a query")
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
        return ResponseEntity.ok(result);
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
//        return ResponseEntity.ok(queryMapper.queryToQueryDto(
//                querystoreService.saveWithoutExecution(databaseId, tableId, data)));
        return null;
    }
//
//    @PutMapping("/table/{tableId}/execute/{queryId}")
//    @ApiOperation(value = "re-executes a query")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Re-Execute a saved query and return the results"),
//            @ApiResponse(code = 404, message = "The database does not exist."),
//            @ApiResponse(code = 405, message = "The container is not running."),
//            @ApiResponse(code = 409, message = "The container image is not supported."),})
//    public ResponseEntity<QueryResultDto> reexecute(@PathVariable Long id,
//                                                    @PathVariable Long queryId,
//                                                    @RequestParam(name = "page", required = false) Integer page,
//                                                    @RequestParam(name = "size", required = false) Integer size)
//            throws DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException,
//            QueryStoreException, QueryMalformedException {
//        return ResponseEntity.ok(querystoreService.reexecute(id, queryId, page, size));
//    }
}
