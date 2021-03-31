package at.tuwien.controller;

import at.tuwien.dto.CopyCSVIntoTableDTO;
import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/database/{id}")
public class QueryController {

    private QueryService service;

    @Autowired
    public QueryController(QueryService service) {
        this.service = service;
    }

    @GetMapping("/query")
    @ApiOperation(value = "List all containers", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all queries."),
    })
    public ResponseEntity<List<QueryResult>> findAll(@PathVariable String id) {
        return null;
    }

    @PostMapping("/query")
    @ApiOperation(value = "Stores a query in normalized way")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query", response = QueryResult.class)})
    public QueryResult create(@PathVariable String id, @RequestBody ExecuteQueryDTO dto) {
        return service.executeQuery(dto);
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
