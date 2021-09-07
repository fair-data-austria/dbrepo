package at.tuwien.endpoints;

import at.tuwien.api.amqp.TupleDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.service.DataService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableId}/data")
public class DataEndpoint {

    private final DataService dataService;

    @Autowired
    public DataEndpoint(DataService dataService) {
        this.dataService = dataService;
    }

    @Transactional
    @PostMapping("/csv")
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processible."),
    })
    public ResponseEntity<?> insertFromFile(@PathVariable("id") Long databaseId,
                                           @PathVariable("tableId") Long tableId,
                                           @Valid @RequestBody TableInsertDto data) throws TableNotFoundException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException {
        dataService.insertCsv(databaseId, tableId, data);
        return ResponseEntity.accepted()
                .build();
    }

    @Transactional
    @PostMapping
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database from AMQP endpoint")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processible."),
    })
    public ResponseEntity<?> insertFromTuple(@PathVariable("id") Long databaseId,
                                    @PathVariable("tableId") Long tableId,
                                    @Valid @RequestBody TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException {
        final Table table = Table.builder()
                .id(tableId)
                .database(Database.builder()
                        .id(databaseId)
                        .build())
                .build();
        dataService.insert(table, data);
        return ResponseEntity.accepted()
                .build();
    }

    @Transactional
    @GetMapping
    @ApiOperation(value = "Get values", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<QueryResultDto> getAll(@PathVariable("id") Long databaseId,
                                                 @PathVariable("tableId") Long tableId, @RequestParam(required = false) Timestamp timestamp) throws TableNotFoundException,
            DatabaseNotFoundException, DatabaseConnectionException, ImageNotSupportedException {
        final QueryResultDto data = dataService.selectAll(databaseId, tableId, timestamp);
        return ResponseEntity.ok(data);
    }


}
