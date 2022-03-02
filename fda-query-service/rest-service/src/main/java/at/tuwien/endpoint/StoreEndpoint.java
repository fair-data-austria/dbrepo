package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.user.User;
import at.tuwien.mapper.UserMapper;
import at.tuwien.querystore.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.StoreService;
import at.tuwien.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/query")
public class StoreEndpoint {

    private final UserMapper userMapper;
    private final QueryMapper queryMapper;
    private final UserService userService;
    private final StoreService storeService;

    @Autowired
    public StoreEndpoint(UserMapper userMapper, QueryMapper queryMapper, UserService userService, StoreService storeService) {
        this.userMapper = userMapper;
        this.queryMapper = queryMapper;
        this.userService = userService;
        this.storeService = storeService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @ApiOperation(value = "List all queries", notes = "Lists all already executed queries")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored query."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<List<QueryDto>> findAll(@NotNull @PathVariable("id") Long id,
                                                  @NotNull @PathVariable("databaseId") Long databaseId) throws QueryStoreException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {
        final List<Query> storedQueries = storeService.findAll(id, databaseId);
        final List<QueryDto> queries = queryMapper.queryListToQueryDtoList(storedQueries);
        queries.forEach(query -> {
            try {
                final User user = userService.findById(query.getCreatedBy());
                query.setCreator(userMapper.userToUserDto(user));
            } catch (UserNotFoundException e) {
                /* already logged */
            }
        });
        return ResponseEntity.ok(queries);
    }

    @GetMapping("/{queryId}")
    @Transactional(readOnly = true)
    @ApiOperation(value = "Find a query", notes = "Find a query")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All queries are listed."),
            @ApiResponse(code = 400, message = "Problem with reading the stored queries."),
            @ApiResponse(code = 404, message = "The database does not exist."),
    })
    public ResponseEntity<QueryDto> find(@NotNull @PathVariable("id") Long id,
                                         @NotNull @PathVariable("databaseId") Long databaseId,
                                         @NotNull @PathVariable Long queryId)
            throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, QueryNotFoundException, ContainerNotFoundException, UserNotFoundException {
        final Query storeQuery = storeService.findOne(id, databaseId, queryId);
        final QueryDto query = queryMapper.queryToQueryDto(storeQuery);
        final User user = userService.findById(query.getCreatedBy());
        query.setCreator(userMapper.userToUserDto(user));
        return ResponseEntity.ok(query);
    }
}
