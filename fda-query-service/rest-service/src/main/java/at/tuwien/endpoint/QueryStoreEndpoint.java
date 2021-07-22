package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import at.tuwien.service.QueryStoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/database/{id}/querystore")
public class QueryStoreEndpoint {

    private QueryStoreService querystoreService;
    private QueryMapper queryMapper;

    @Autowired
    public QueryStoreEndpoint(QueryStoreService queryService, QueryMapper queryMapper) {
        this.querystoreService = queryService;
        this.queryMapper = queryMapper;
    }

    @Transactional
    @GetMapping()
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored queries."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryResultDto>> findAll(@PathVariable Long id) throws DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, QueryMalformedException {
        final List<Query> queries = querystoreService.findAll(id);
        return ResponseEntity.ok(queries.stream()
                .map(queryMapper::queryToQueryDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping()
    @ApiOperation(value = "Creates the query Story")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created the Querystore successfully"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<?> create(@PathVariable Long id) throws ImageNotSupportedException, DatabaseNotFoundException, QueryStoreException, SQLException {
        querystoreService.create(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
