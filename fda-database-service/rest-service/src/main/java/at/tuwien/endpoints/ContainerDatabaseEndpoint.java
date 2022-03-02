package at.tuwien.endpoints;

import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.service.impl.MariaDbServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/container/{id}/database")
public class ContainerDatabaseEndpoint {

    private final DatabaseMapper databaseMapper;
    private final MariaDbServiceImpl databaseService;

    @Autowired
    public ContainerDatabaseEndpoint(DatabaseMapper databaseMapper, MariaDbServiceImpl databaseService) {
        this.databaseMapper = databaseMapper;
        this.databaseService = databaseService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @ApiOperation(value = "List all databases", notes = "Currently a container supports only databases of the same image, e.g. there is one PostgreSQL engine running with multiple databases inside a container.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All databases running in all containers are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all databases."),
    })
    public ResponseEntity<List<DatabaseBriefDto>> findAll(@NotBlank @PathVariable("id") Long id) {
        final List<DatabaseBriefDto> databases = databaseService.findAll(id)
                .stream()
                .map(databaseMapper::databaseToDatabaseBriefDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(databases);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "Creates a new database in a container", notes = "Creates a new database in a container. Note that the backend distincts between numerical (req: categories), nominal (req: max_length) and categorical (req: max_length, siUnit, min, max, mean, median, standard_deviation, histogram) column types.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The database was successfully created."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully"),
            @ApiResponse(code = 401, message = "Not authorized to create a database."),
            @ApiResponse(code = 404, message = "Container does not exist with this id."),
            @ApiResponse(code = 405, message = "Unable to connect to database within container."),
    })
    public ResponseEntity<DatabaseDto> create(@NotBlank @PathVariable("id") Long id,
                                              @Valid @RequestBody DatabaseCreateDto createDto)
            throws ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException,
            AmqpException, ContainerConnectionException, UserNotFoundException {
        final Database database = databaseService.create(id, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseMapper.databaseToDatabaseDto(database));
    }

    @GetMapping("/{databaseId}")
    @Transactional(readOnly = true)
    @ApiOperation(value = "Get all information about a database")
    @ApiResponses({
            @ApiResponse(code = 200, message = "The database information is displayed."),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseDto> findById(@NotBlank @PathVariable("id") Long id,
                                                @NotBlank @PathVariable Long databaseId)
            throws DatabaseNotFoundException, ContainerNotFoundException {
        return ResponseEntity.ok(databaseMapper.databaseToDatabaseDto(databaseService.findById(id, databaseId)));
    }

    @PutMapping("/{databaseId}")
    @Transactional
    @ApiOperation(value = "Updates information about the database")
    @ApiResponses({
            @ApiResponse(code = 200, message = "The database information is displayed."),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
    })
    public ResponseEntity<DatabaseDto> update(@NotBlank @PathVariable("id") Long id,
                                              @NotBlank @PathVariable Long databaseId,
                                              @Valid @RequestBody DatabaseModifyDto metadata)
            throws UserNotFoundException, ContainerNotFoundException, DatabaseNotFoundException {
        return ResponseEntity.accepted()
                .body(databaseMapper.databaseToDatabaseDto(databaseService.update(id, databaseId, metadata)));
    }

    @DeleteMapping("/{databaseId}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_DATA_STEWARD')")
    @ApiOperation(value = "Delete a database")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The database was successfully deleted."),
            @ApiResponse(code = 400, message = "The SQL statement contains invalid syntax"),
            @ApiResponse(code = 401, message = "Not authorized to delete a database."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database."),
            @ApiResponse(code = 405, message = "Unable to connect to database within container."),
    })
    public ResponseEntity<?> delete(@NotBlank @PathVariable("id") Long id,
                                    @NotBlank @PathVariable Long databaseId) throws DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseMalformedException, AmqpException, ContainerConnectionException,
            ContainerNotFoundException {
        databaseService.delete(id, databaseId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

}
