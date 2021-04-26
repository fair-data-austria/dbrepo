package at.tuwien.endpoint;

import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.dto.QueryDto;
import at.tuwien.entity.Query;
import at.tuwien.entity.QueryResult;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLSyntaxErrorException;
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
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) throws DatabaseNotFoundException, ImageNotSupportedException {

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
    public ResponseEntity create(@PathVariable Long id) throws ImageNotSupportedException, DatabaseConnectionException, DatabaseNotFoundException {
        queryService.create(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/query")
    @ApiOperation(value = "executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public Response modify(@PathVariable Long id, @RequestBody ExecuteQueryDTO dto) throws DatabaseNotFoundException, ImageNotSupportedException, SQLSyntaxErrorException {
        QueryResult qr = queryService.executeStatement(id, queryMapper.queryDTOtoQuery(dto));

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    @PutMapping("/query/version/{timestamp}")
    @ApiOperation(value = "executes a query with a given timestamp")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query with Timestamp", response = Response.class)})
    public Response modify(@PathVariable Long id, @PathVariable String timestamp, @RequestBody ExecuteQueryDTO dto) throws DatabaseNotFoundException, ImageNotSupportedException, SQLSyntaxErrorException {
        queryService.executeStatement(id, queryMapper.queryDTOtoQuery(dto));

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}