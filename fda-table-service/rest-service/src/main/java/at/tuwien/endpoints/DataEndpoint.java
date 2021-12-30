package at.tuwien.endpoints;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import at.tuwien.service.DataService;
import at.tuwien.service.TextDataService;
import com.opencsv.exceptions.CsvException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Instant;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableId}/data")
public class DataEndpoint {

    private final DataService dataService;
    private final TextDataService textDataService;

    @Autowired
    public DataEndpoint(DataService dataService, TextDataService textDataService) {
        this.dataService = dataService;
        this.textDataService = textDataService;
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
            @ApiResponse(code = 422, message = "The csv was not processable."),
    })
    public ResponseEntity<?> insert(@PathVariable("id") Long databaseId,
                                    @PathVariable Long tableId,
                                    @RequestParam String location) throws TableNotFoundException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException {
        final TableCsvDto data;
        try {
            data = textDataService.read(databaseId, tableId, location);
        } catch (IOException | CsvException e) {
            throw new FileStorageException("File not readable", e);
        }
        dataService.insert(databaseId, tableId, data);
        return ResponseEntity.accepted()
                .build();
    }

    @Transactional
    @GetMapping("/csv")
    @ApiOperation(value = "Download table", notes = "Download Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "The csv is downloaded."),
            // TODO
    })
    public ResponseEntity<Resource> export(@PathVariable("id") Long databaseId,
                                           @PathVariable Long tableId,
                                           @RequestParam(required = false) Instant timestamp) throws TableNotFoundException,
            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException,
            FileStorageException, PaginationException {
        return ResponseEntity.ok(textDataService.write(databaseId, tableId, timestamp));
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
            @ApiResponse(code = 422, message = "The csv was not processable."),
    })
    public ResponseEntity<?> insert(@PathVariable("id") Long databaseId,
                                    @PathVariable Long tableId,
                                    @Valid @RequestBody TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, TableNotFoundException, DatabaseNotFoundException {
        dataService.insert(databaseId, tableId, data);
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
                                                 @PathVariable Long tableId,
                                                 @RequestParam(required = false) Instant timestamp,
                                                 @RequestParam(required = false) Long page,
                                                 @RequestParam(required = false) Long size)
            throws TableNotFoundException, DatabaseNotFoundException, DatabaseConnectionException,
            ImageNotSupportedException, TableMalformedException, PaginationException {
        final QueryResultDto data = dataService.findAll(databaseId, tableId, timestamp, page, size);
        return ResponseEntity.ok(data);
    }


}
