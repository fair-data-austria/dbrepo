package at.tuwien.endpoint;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.sql.SQLException;
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

    @Transactional
    @PutMapping("/query")
    @ApiOperation(value = "executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executed the query, Saved it and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> execute(@PathVariable Long id, @RequestBody ExecuteQueryDto dto)
            throws DatabaseNotFoundException, ImageNotSupportedException, SQLException,
            JSQLParserException, QueryMalformedException, QueryStoreException {
        final QueryResultDto response = queryService.execute(id, queryMapper.queryDTOtoQuery(dto));
        return ResponseEntity.ok(response);
    }

    @Transactional
    @GetMapping("/query/{queryId}")
    @ApiOperation(value = "re-executes a query")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Re-Execute a saved query and return the results"),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),})
    public ResponseEntity<QueryResultDto> reexecute(@PathVariable Long id, @PathVariable Long queryId, @RequestParam(name="page", required= false) Integer page, @RequestParam(name = "size", required = false) Integer size)
            throws DatabaseNotFoundException, ImageNotSupportedException, SQLException,
            JSQLParserException, QueryMalformedException, QueryStoreException {
        final QueryResultDto response = queryService.reexecute(id, queryId, page, size);
        return ResponseEntity.ok(response);
    }



}
