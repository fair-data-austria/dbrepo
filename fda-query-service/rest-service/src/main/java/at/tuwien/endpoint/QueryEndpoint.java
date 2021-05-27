package at.tuwien.endpoint;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/database/{id}")
public class QueryEndpoint {

    private QueryService queryService;
    private QueryMapper queryMapper;

    @Autowired
    public QueryEndpoint(QueryService queryService, QueryMapper queryMapper) {
        this.queryService = queryService;
        this.queryMapper = queryMapper;
    }

    @GetMapping("/query")
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored queries."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) throws DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, QueryMalformedException {
        final List<Query> queries = queryService.findAll(id);
        return ResponseEntity.ok(queries.stream()
                .map(queryMapper::queryToQueryDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping("/query")
    @ApiOperation(value = "Creates the query Story")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created the Querystore successfully"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<?> create(@PathVariable Long id) throws ImageNotSupportedException,
            DatabaseConnectionException, DatabaseNotFoundException {
        queryService.create(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/query")
    @ApiOperation(value = "executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> modify(@PathVariable Long id, @RequestBody ExecuteQueryDto dto)
            throws DatabaseNotFoundException, ImageNotSupportedException, SQLFeatureNotSupportedException,
            JSQLParserException {
        final QueryResultDto response = queryService.executeStatement(id, queryMapper.queryDTOtoQuery(dto));
        return ResponseEntity.ok(response);
    }


    @PutMapping("/query/version/{timestamp}")
    @ApiOperation(value = "executes a query with a given timestamp")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query with Timestamp")})
    public ResponseEntity<QueryResultDto> modify(@PathVariable Long id, @PathVariable String timestamp, @RequestBody ExecuteQueryDto dto)
            throws DatabaseNotFoundException, ImageNotSupportedException, SQLFeatureNotSupportedException,
            JSQLParserException {
        final QueryResultDto response = queryService.executeStatement(id, queryMapper.queryDTOtoQuery(dto));
        return ResponseEntity.ok(response);
    }

}
