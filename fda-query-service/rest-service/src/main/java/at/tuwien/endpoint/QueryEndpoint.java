package at.tuwien.endpoint;

import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.dto.QueryDto;
import at.tuwien.entity.Query;
import at.tuwien.entity.QueryResult;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
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
import java.util.List;

@RestController
@RequestMapping("/api/database/{id}")
public class QueryEndpoint {

    private QueryService service;

    @Autowired
    public QueryEndpoint(QueryService service) {
        this.service = service;
    }

    @GetMapping("/query")
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all queries."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) {

        return null;
    }

    @PostMapping("/query")
    @ApiOperation(value = "Creates the query Story")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created the Querystore successfully"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity create(@PathVariable Long id) throws ImageNotSupportedException, DatabaseConnectionException, DatabaseNotFoundException {
        service.create(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/query")
    @ApiOperation(value = "executes a query")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query with Timestamp", response = Response.class)})
    public Response modify(@PathVariable String id, @RequestBody ExecuteStatementDTO dto) {
        service.executeStatement(dto);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    @PutMapping("/query/version/{timestamp}")
    @ApiOperation(value = "executes a query with a given timestamp")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query with Timestamp", response = Response.class)})
    public Response modify(@PathVariable String id, @PathVariable String timestamp, @RequestBody ExecuteStatementDTO dto) {
        service.executeStatement(dto);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
