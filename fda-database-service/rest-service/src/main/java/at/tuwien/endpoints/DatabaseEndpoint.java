package at.tuwien.endpoints;

import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.database.Database;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/database")
public class DatabaseEndpoint {

    private final DatabaseMapper databaseMapper;
    private final DatabaseService databaseService;

    @Autowired
    public DatabaseEndpoint(DatabaseMapper databaseMapper, DatabaseService databaseService) {
        this.databaseMapper = databaseMapper;
        this.databaseService = databaseService;
    }

    @Transactional
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

    @Transactional
    @PostMapping("/")
    @ApiOperation(value = "Creates a new database in a container", notes = "Creates a new database in a container. Note that the backend distincts between numerical (req: categories), nominal (req: max_length) and categorical (req: max_length, siUnit, min, max, mean, median, standard_deviation, histogram) column types.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The database was successfully created."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully"),
            @ApiResponse(code = 401, message = "Not authorized to create a database."),
            @ApiResponse(code = 404, message = "Container does not exist with this id."),
            @ApiResponse(code = 405, message = "Unable to connect to database within container."),
    })
    public ResponseEntity<DatabaseBriefDto> create(@Valid @RequestBody DatabaseCreateDto createDto)
            throws ImageNotSupportedException, ContainerNotFoundException, SQLException {
        final Database database = databaseService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseMapper.databaseToDatabaseBriefDto(database));
    }

    @Transactional
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
    @ApiOperation(value = "Modify a database")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully modified."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required for column type."),
            @ApiResponse(code = 401, message = "Not authorized to change a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseBriefDto> modify(@NotBlank @PathVariable Long id, @Valid @RequestBody DatabaseModifyDto modifyDto) throws SQLException, DatabaseNotFoundException, ImageNotSupportedException {
        final DatabaseBriefDto database = databaseMapper.databaseToDatabaseBriefDto(databaseService.modify(modifyDto));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(database);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a database")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully deleted."),
            @ApiResponse(code = 400, message = "The SQL statement contains invalid syntax"),
            @ApiResponse(code = 401, message = "Not authorized to delete a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
            @ApiResponse(code = 405, message = "Unable to connect to database within container."),
    })
    public ResponseEntity<?> delete(@NotBlank @PathVariable Long id) throws DatabaseNotFoundException,
            ImageNotSupportedException, SQLException {
        databaseService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

}
