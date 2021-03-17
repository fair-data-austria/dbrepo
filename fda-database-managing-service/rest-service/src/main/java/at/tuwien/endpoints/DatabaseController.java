package at.tuwien.endpoints;

import at.tuwien.dto.database.DatabaseBriefDto;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.dto.database.DatabaseChangeDto;
import at.tuwien.dto.database.DatabaseDto;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<DatabaseBriefDto>> findAll() {
//        LOGGER.debug("getting a list of created databases");
//        return service.findAllCreatedDatabases();
        return null;
    }

    @PostMapping("/database")
    @ApiOperation(value = "Creates a new database", notes = "Creates a new database in a container")
    public ResponseEntity<DatabaseDto> create(@RequestBody DatabaseCreateDto dto) {
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/database/{id}")
    @ApiOperation(value = "Get all informations about a database")
    public ResponseEntity<DatabaseDto> findById(@RequestParam String id) {
        return null;
    }

    @PutMapping("/database/{id}")
    @ApiOperation(value = "Change the state of a database")
    public ResponseEntity<DatabaseDto> modify(@RequestParam String id, @RequestBody DatabaseChangeDto changeDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/database/{id}")
    @ApiOperation(value = "Delete a database")
    public ResponseEntity delete(@RequestParam String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
