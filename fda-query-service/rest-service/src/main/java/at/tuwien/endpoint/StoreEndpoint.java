package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.exception.*;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/database/{id}/store")
public class StoreEndpoint {

    private StoreService storeService;

    @Autowired
    public StoreEndpoint(StoreService storeService) {
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
        return ResponseEntity.ok(storeService.findAll(id));
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
        return ResponseEntity.ok(storeService.findOne(id, queryId));
    }
}
