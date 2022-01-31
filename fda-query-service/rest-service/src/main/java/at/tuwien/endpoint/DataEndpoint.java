package at.tuwien.endpoint;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import at.tuwien.service.CommaValueService;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/table/{tableId}/data")
public class DataEndpoint {

    private final QueryService queryService;
    private final CommaValueService commaValueService;

    @Autowired
    public DataEndpoint(QueryService queryService, CommaValueService commaValueService) {
        this.queryService = queryService;
        this.commaValueService = commaValueService;
    }

    @Transactional
    @PostMapping
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database. When the location string is set, the data argument is ignored and the location is used as data input")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processible."),
    })
    public ResponseEntity<Integer> insert(@NotNull @PathVariable("id") Long id,
                                          @NotNull @PathVariable("databaseId") Long databaseId,
                                          @NotNull @PathVariable("tableId") Long tableId,
                                          @RequestParam(required = false) String location,
                                          @Valid @RequestBody(required = false) TableCsvDto data) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException, TableMalformedException, ImageNotSupportedException {
        if ((location == null && data == null) || (location != null && data != null)) {
            log.error("Either location/data must be non-null (not both)");
            throw new TableMalformedException("Either location/data must be non-null");
        }
        if (location != null && !location.isEmpty()) {
            log.info("Insert data from location {} into database id {}", location, databaseId);
            return ResponseEntity.accepted()
                    .body(queryService.insert(databaseId, tableId, location));
        }
        return ResponseEntity.accepted()
                .body(queryService.insert(databaseId, tableId, data));
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
    public ResponseEntity<QueryResultDto> getAll(@NotNull @PathVariable("id") Long id,
                                                 @NotNull @PathVariable("databaseId") Long databaseId,
                                                 @NotNull @PathVariable("tableId") Long tableId,
                                                 @RequestParam(required = false) Instant timestamp,
                                                 @RequestParam(required = false) Long page,
                                                 @RequestParam(required = false) Long size)
            throws TableNotFoundException, DatabaseNotFoundException, DatabaseConnectionException,
            ImageNotSupportedException, TableMalformedException, PaginationException {
        if ((page == null && size != null) || (page != null && size == null)) {
            log.error("Cannot perform pagination with only one of page/size set.");
            log.debug("invalid pagination specification, one of page/size is null, either both should be null or none.");
            throw new PaginationException("Invalid pagination parameters");
        }
        if (page != null && page < 0) {
            throw new PaginationException("Page number cannot be lower than 0");
        }
        if (size != null && size <= 0) {
            throw new PaginationException("Page number cannot be lower or equal to 0");
        }
        final QueryResultDto response = queryService.findAll(databaseId, tableId, timestamp, page, size);
        return ResponseEntity.ok(response);
    }

    @Transactional
    @GetMapping(value = "/export")
    @ApiOperation(value = "Download export", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<InputStreamResource> export(@NotNull @PathVariable("id") Long id,
                                                      @NotNull @PathVariable("databaseId") Long databaseId,
                                                      @NotNull @PathVariable("tableId") Long tableId,
                                                      @RequestParam(required = false) Instant timestamp)
            throws TableNotFoundException, DatabaseNotFoundException, DatabaseConnectionException,
            ImageNotSupportedException, TableMalformedException, FileStorageException, PaginationException {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        final InputStreamResource data = commaValueService.export(databaseId, tableId, timestamp);
        return ResponseEntity.ok(data);
    }


}
