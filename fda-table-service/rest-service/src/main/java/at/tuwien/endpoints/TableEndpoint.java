package at.tuwien.endpoints;

import at.tuwien.api.database.table.*;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.service.TableService;
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
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/table")
public class TableEndpoint {

    private final TableService tableService;
    private final TableMapper tableMapper;

    @Autowired
    public TableEndpoint(TableService tableService, TableMapper tableMapper) {
        this.tableService = tableService;
        this.tableMapper = tableMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @ApiOperation(value = "List all tables", notes = "Lists the tables in the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
    })
    public ResponseEntity<List<TableBriefDto>> findAll(@NotNull @PathVariable("id") Long id,
                                                       @NotNull @PathVariable("databaseId") Long databaseId)
            throws DatabaseNotFoundException {
        return ResponseEntity.ok(tableService.findAll(id, databaseId)
                .stream()
                .map(tableMapper::tableToTableBriefDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "Create a table", notes = "Creates a new table for a database, requires a running container.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The table was created."),
            @ApiResponse(code = 400, message = "The creation form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to create a tables."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The table name already exists."),
    })
    public ResponseEntity<TableBriefDto> create(@NotNull @PathVariable("id") Long id,
                                                @NotNull @PathVariable("databaseId") Long databaseId,
                                                @NotNull @Valid @RequestBody TableCreateDto createDto)
            throws ImageNotSupportedException, DatabaseNotFoundException, DataProcessingException,
            ArbitraryPrimaryKeysException, TableMalformedException, TableNameExistsException,
            ContainerNotFoundException {
        final Table table = tableService.createTable(id, databaseId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableMapper.tableToTableBriefDto(table));
    }


    @GetMapping("/{tableId}")
    @Transactional(readOnly = true)
    @ApiOperation(value = "Get information about table", notes = "Lists the information of a table from the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
            @ApiResponse(code = 404, message = "Table not found in metadata database."),
    })
    public ResponseEntity<TableDto> findById(@NotNull @PathVariable("id") Long id,
                                             @NotNull @PathVariable("databaseId") Long databaseId,
                                             @NotNull @PathVariable("tableId") Long tableId)
            throws TableNotFoundException, DatabaseNotFoundException, ContainerNotFoundException {
        final Table table = tableService.findById(id, databaseId, tableId);
        return ResponseEntity.ok(tableMapper.tableToTableDto(table));
    }

    @PutMapping("/{tableId}")
    @Transactional
    @ApiOperation(value = "Update a table", notes = "Update a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The update form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    public ResponseEntity<TableBriefDto> update(@NotNull @PathVariable("id") Long id,
                                                @NotNull @PathVariable("databaseId") Long databaseId,
                                                @NotNull @PathVariable("tableId") Long tableId) {
        // TODO
        return ResponseEntity.unprocessableEntity().body(new TableBriefDto());
    }

    @DeleteMapping("/{tableId}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_DATA_STEWARD')")
    @ApiOperation(value = "Delete a table", notes = "Delete a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the table."),
            @ApiResponse(code = 401, message = "Not authorized to delete tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    @ResponseStatus(HttpStatus.OK)
    public void delete(@NotNull @PathVariable("id") Long id,
                       @NotNull @PathVariable("databaseId") Long databaseId,
                       @NotNull @PathVariable("tableId") Long tableId)
            throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException, ContainerNotFoundException {
        tableService.deleteTable(id, databaseId, tableId);
    }

}
