package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/database/{id}")
public class QueryEndpoint {

    private final QueryMapper queryMapper;
    private final QueryService queryService;

    @Autowired
    public QueryEndpoint(QueryMapper queryMapper, QueryService queryService) {
        this.queryMapper = queryMapper;
        this.queryService = queryService;
    }

    @GetMapping("/{queryId}")
    @ApiOperation(value = "Find a query", notes = "Find a query")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored queries."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<QueryDto> find(@PathVariable Long id, @PathVariable Long queryId)
            throws QueryNotFoundException {
        return ResponseEntity.ok(queryMapper.queryToQueryDto(queryService.findById(queryId)));
    }

    @GetMapping
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored query."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@PathVariable Long id) {
        final List<Query> queries = queryService.findAll(id);
        return ResponseEntity.ok(queries.stream()
                .map(queryMapper::queryToQueryDto)
                .collect(Collectors.toList()));
    }


}
