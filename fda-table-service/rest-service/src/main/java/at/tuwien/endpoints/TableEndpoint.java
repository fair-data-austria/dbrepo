package at.tuwien.endpoints;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvInformationDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.service.TableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}")
public class TableEndpoint {

    private final TableService tableService;
    private final TableMapper tableMapper;
    private final QueryMapper queryResultMapper;

    @Autowired
    public TableEndpoint(TableService tableService, TableMapper tableMapper, QueryMapper queryResultMapper) {
        this.tableService = tableService;
        this.tableMapper = tableMapper;
        this.queryResultMapper = queryResultMapper;
    }

    @Transactional
    @GetMapping("/table")
    @ApiOperation(value = "List all tables", notes = "Lists the tables in the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
    })
    public ResponseEntity<List<TableBriefDto>> findAll(@PathVariable("id") Long databaseId)
            throws DatabaseNotFoundException, TableNotFoundException {
        final List<Table> tables = tableService.findAll(databaseId);
        log.debug("received tables {}", tables);
        return ResponseEntity.ok(tables.stream()
                .map(tableMapper::tableToTableBriefDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    @PostMapping("/table")
    @ApiOperation(value = "Create a table", notes = "Creates a new table for a database, requires a running container.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The table was created."),
            @ApiResponse(code = 400, message = "The creation form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to create a tables."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),
            @ApiResponse(code = 422, message = "The ."),
    })
    public ResponseEntity<TableBriefDto> create(@PathVariable("id") Long databaseId,
                                                @Valid @RequestBody TableCreateDto createDto)
            throws ImageNotSupportedException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, DataProcessingException {
        final Table table = tableService.create(databaseId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableMapper.tableToTableBriefDto(table));
    }

    @Transactional
    @PostMapping("/table/csv")
    @ApiOperation(value = "Create a table", notes = "Creates a file, which is given as a multipart file.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The table was created."),
            @ApiResponse(code = 400, message = "The creation form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to create a tables."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),
    })
    public ResponseEntity<TableDto> createViaCsv(@PathVariable("id") Long databaseId, @RequestPart("file") MultipartFile file, @RequestPart TableCsvInformationDto headers) {
        final Table table = tableService.create(databaseId, file, headers);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableMapper.tableToTableDto(table));
    }

    @Transactional
    @PostMapping("/table/csv/local")
    @ApiOperation(value = "Create a table", notes = "This is done by saving a file on the shared docker filesystem and then sending the link to the file.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The table was created."),
            @ApiResponse(code = 400, message = "The creation form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to create a tables."),
            @ApiResponse(code = 404, message = "The database does not exist."),
            @ApiResponse(code = 405, message = "The container is not running."),
            @ApiResponse(code = 409, message = "The container image is not supported."),
    })
    public ResponseEntity<TableDto> createViaCsv(@PathVariable("id") Long databaseId, @Valid @RequestBody TableCsvInformationDto tableCSVInformation) throws IOException {
        final Table table = tableService.create(databaseId, tableCSVInformation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tableMapper.tableToTableDto(table));
    }


    @Transactional
    @GetMapping("/table/{tableId}")
    @ApiOperation(value = "List all tables", notes = "Lists the tables in the metadata database for this database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
            @ApiResponse(code = 404, message = "Table not found in metadata database."),
    })
    public ResponseEntity<TableDto> findById(@PathVariable("id") Long databaseId, @PathVariable("tableId") Long tableId)
            throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException {
        final Table table = tableService.findById(databaseId, tableId);
        return ResponseEntity.ok(tableMapper.tableToTableDto(table));
    }

    @PutMapping("/table/{tableId}")
    @ApiOperation(value = "Update a table", notes = "Update a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The update form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    public ResponseEntity<TableBriefDto> update(@PathVariable("id") Long databaseId,
                                                @PathVariable("tableId") Long tableId) {
        // TODO
        return ResponseEntity.unprocessableEntity().body(new TableBriefDto());
    }

    @DeleteMapping("/table/{tableId}")
    @ApiOperation(value = "Delete a table", notes = "Delete a table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the table."),
            @ApiResponse(code = 401, message = "Not authorized to delete tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
    })
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long databaseId, @PathVariable("tableId") Long tableId)
            throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DataProcessingException, DatabaseNotFoundException, ImageNotSupportedException {
        tableService.delete(databaseId, tableId);
    }

    @Transactional
    @PostMapping("/table/{tableId}")
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 422, message = "The csv was not processible."),
    })
    /* FIXME: this should be in a different endpoint */
    public ResponseEntity<QueryResultDto> insert(@PathVariable("id") Long databaseId,
                                                 @PathVariable("tableId") Long tableId,
                                                 @RequestParam("file") MultipartFile file) throws Exception {
        final QueryResultDto queryResult = tableService.insert(databaseId, tableId, file);
        return ResponseEntity.ok(queryResultMapper.queryResultToQueryResultDto(queryResult));
    }

    @Transactional
    @GetMapping("/table/{tableId}/data")
    @ApiOperation(value = "show data", notes = "Show all the data for a table")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All tables are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all tables."),
    })
    /* FIXME: this should be a different endpoint */
    public ResponseEntity<QueryResultDto> showData(@PathVariable("id") Long databaseId,
                                                   @PathVariable("tableId") Long tableId)
            throws DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException,
            DatabaseConnectionException, DataProcessingException {
        final QueryResultDto queryResult = tableService.showData(databaseId, tableId);
        return ResponseEntity.ok(queryResultMapper.queryResultToQueryResultDto(queryResult));
    }

}
