package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/database/{id}/query")
public class StoreEndpoint {

    private final QueryMapper queryMapper;
    private final StoreService storeService;

    @Autowired
    public StoreEndpoint(QueryMapper queryMapper, StoreService storeService) {
        this.queryMapper = queryMapper;
        this.storeService = storeService;
    }

    @GetMapping
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored query."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) throws QueryStoreException,
            DatabaseNotFoundException, ImageNotSupportedException {
        final List<Query> queries = storeService.findAll(id);
        return ResponseEntity.ok(queryMapper.queryListToQueryDtoList(queries));
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
            throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, QueryNotFoundException {
        final Query query = storeService.findOne(id, queryId);
        return ResponseEntity.ok(queryMapper.queryToQueryDto(query));
    }
}
