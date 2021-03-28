package at.tuwien.endpoints;

import at.tuwien.dto.database.DatabaseBriefDto;
import at.tuwien.dto.database.DatabaseChangeDto;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseMapper databaseMapper;
    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseMapper databaseMapper, DatabaseService databaseService) {
        this.databaseMapper = databaseMapper;
        this.databaseService = databaseService;
    }

    @GetMapping("/")
    @ApiOperation(value = "List all databases", notes = "Currently a container supports only databases of the same image, e.g. there is one PostgreSQL engine running with multiple databases inside a container.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All databases running in all containers are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all databases."),
    })
    public ResponseEntity<List<DatabaseBriefDto>> findAll() {
        final List<DatabaseBriefDto> databases = databaseService.findAll()
                .stream()
                .map(databaseMapper::databaseToDatabaseBriefDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(databases);
    }

    @PostMapping("/")
    @ApiOperation(value = "Creates a new database in a container", notes = "Creates a new database in a container. Note that the backend distincts between numerical (req: categories), nominal (req: max_length) and categorical (req: max_length, siUnit, min, max, mean, median, standard_deviation, histogram) column types.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The database was successfully created."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required for column type."),
            @ApiResponse(code = 401, message = "Not authorized to create a database"),
    })
    public ResponseEntity<DatabaseBriefDto> create(@Valid @RequestBody DatabaseCreateDto createDto)
            throws ImageNotSupportedException, DatabaseConnectionException, DatabaseMalformedException {
        log.debug("received {}", createDto);
        final Database database = databaseService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseMapper.databaseToDatabaseBriefDto(database));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get all informations about a database")
    @ApiResponses({
            @ApiResponse(code = 200, message = "The database information is displayed."),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseBriefDto> findById(@NotBlank @PathVariable Long id) throws DatabaseNotFoundException {
        final DatabaseBriefDto database = databaseMapper.databaseToDatabaseBriefDto(databaseService.findById(id));
        return ResponseEntity.ok(database);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Modify a database (not part of sprint 1)")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully modified."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required for column type."),
            @ApiResponse(code = 401, message = "Not authorized to change a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseBriefDto> modify(@NotBlank @PathVariable Long databaseId, @Valid @RequestBody DatabaseChangeDto changeDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a database")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully deleted."),
            @ApiResponse(code = 401, message = "Not authorized to delete a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity delete(@NotBlank @PathVariable Long databaseId) throws DatabaseNotFoundException {
        databaseService.delete(databaseId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

}
