package at.tuwien.endpoints;

import at.tuwien.dto.database.DatabaseBriefDto;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.dto.database.DatabaseChangeDto;
import at.tuwien.dto.database.DatabaseDto;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DatabaseController {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/database")
    @ApiOperation(value = "List all databases", notes = "Currently a container supports only databases of the same image, e.g. there is one PostgreSQL engine running with multiple databases inside a container.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All databases are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all databases."),
    })
    public ResponseEntity<List<DatabaseBriefDto>> findAll() {
//        LOGGER.debug("getting a list of created databases");
//        return service.findAllCreatedDatabases();
        return null;
    }

    @PostMapping("/database")
    @ApiOperation(value = "Creates a new database", notes = "Creates a new database in a container. Note that the backend distincts between numerical (req: categories), nominal (req: max_length) and categorical (req: max_length, siUnit, min, max, mean, median, standard_deviation, histogram) column types.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The database was successfully created."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required for column type."),
            @ApiResponse(code = 401, message = "Not authorized to create a database"),
    })
    public ResponseEntity<DatabaseDto> create(@Valid @RequestBody DatabaseCreateDto dto) {
//        LOGGER.debug("creating new database");
//        boolean succeed = service.createDatabase(dto);
//        if (succeed) {
//            return Response
//                    .status(Response.Status.CREATED)
//                    .entity("Database container successfully created and started!")
//                    .type(MediaType.APPLICATION_JSON)
//                    .build();
//        }
//        return Response
//                .status(Response.Status.INTERNAL_SERVER_ERROR)
//                .type(MediaType.APPLICATION_JSON)
//                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @GetMapping("/database/{id}")
    @ApiOperation(value = "Get all informations about a database")
    @ApiResponses({
            @ApiResponse(code = 200, message = "The database information is displayed."),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseDto> findById(@RequestParam String id) {
        return null;
    }

    @PutMapping("/database/{id}")
    @ApiOperation(value = "Modify a database (not part of sprint 1)")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully modified."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required for column type."),
            @ApiResponse(code = 401, message = "Not authorized to change a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseDto> modify(@RequestParam String id, @RequestBody DatabaseChangeDto changeDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/database/{id}")
    @ApiOperation(value = "Delete a database")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully deleted."),
            @ApiResponse(code = 401, message = "Not authorized to delete a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity delete(@RequestParam String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
