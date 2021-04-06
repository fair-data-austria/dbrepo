package at.tuwien.endpoints;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.TableMapper;
import at.tuwien.service.TableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/database/{id}")
public class TableEndpoint {

    private final TableService tableService;
    private final TableMapper tableMapper;

    @Autowired
    public TableEndpoint(TableService tableService, TableMapper tableMapper) {
        this.tableService = tableService;
        this.tableMapper = tableMapper;
    }

    @GetMapping("/table")
    @ApiOperation(value = "List all tables", notes = "Lists the tables in the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
    })
    public ResponseEntity<List<TableBriefDto>> findAll(@PathVariable("id") Long databaseId)
            throws DatabaseNotFoundException {
        final List<Table> tables = tableService.findAll(databaseId);
        log.debug("received tables {}", tables);
        return ResponseEntity.ok(tables.stream()
                .map(tableMapper::tableToTableBriefDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/table")
    @ApiOperation(value = "Create a table", notes = "Creates a new table for a database, requires a running container. For the colum definition use the following example: [{\"name\": \"Ticker Symbol\", \"primaryKey\": true, \"type\": \"STRING\", \"nullAllowed\": false, \"checkExpression\": null, \"foreignKey\": null},{\"name\": \"Accounts Payable\", \"primaryKey\": false, \"type\": \"NUMBER\", \"nullAllowed\": false, \"checkExpression\": \"Accounts Payable > 0\", \"foreignKey\": null},{\"name\": \"Company\", \"primaryKey\": false, \"type\": \"STRING\", \"nullAllowed\": false, \"checkExpression\": null, \"foreignKey\": null}]")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The table was created."),
            @ApiResponse(code = 400, message = "The creation form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to create a tables."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),
    })
    public ResponseEntity<TableBriefDto> create(@PathVariable("id") Long databaseId, @RequestBody TableCreateDto createDto)
            throws ImageNotSupportedException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException {
        final Table table = tableService.create(databaseId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableMapper.tableToTableBriefDto(table));
    }

    @GetMapping("/table/{tableId}")
    @ApiOperation(value = "List all tables", notes = "Lists the tables in the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
    })
    public ResponseEntity<List<TableDto>> findById(@PathVariable("id") Long databaseId, @PathVariable("tableId") Long tableId) {
        final List<Table> tables = tableService.findById(databaseId, tableId);
        return ResponseEntity.ok(tables.stream()
                .map(tableMapper::tableToTableDto)
                .collect(Collectors.toList()));
    }

    @PutMapping("/table/{tableId}")
    @ApiOperation(value = "Update a table", notes = "Update a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The update form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    public ResponseEntity<TableBriefDto> update(@PathVariable("id") Long databaseId, @PathVariable("tableId") Long tableId) {
        // TODO
        return ResponseEntity.ok(new TableBriefDto());
    }

    @DeleteMapping("/table/{tableId}")
    @ApiOperation(value = "Delete a table", notes = "Delete a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    public ResponseEntity delete(@PathVariable("id") Long databaseId, @PathVariable("tableId") Long tableId) {
        // TODO
        return ResponseEntity.ok(null);
    }


}
