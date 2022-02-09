package at.tuwien.endpoint;

import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import at.tuwien.service.QueryService;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/table/{tableId}/data")
public class TableDataEndpoint {

    private final QueryService queryService;
    private final StoreService storeService;

    @Autowired
    public TableDataEndpoint(QueryService queryService, StoreService storeService) {
        this.queryService = queryService;
        this.storeService = storeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @Transactional
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database. When the location string is set, the data argument is ignored and the location is used as data input")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processable."),
    })
    public ResponseEntity<Integer> insert(@NotNull @PathVariable("id") Long id,
                                          @NotNull @PathVariable("databaseId") Long databaseId,
                                          @NotNull @PathVariable("tableId") Long tableId,
                                          @Valid @RequestBody TableCsvDto data)
            throws TableNotFoundException, DatabaseNotFoundException, FileStorageException, TableMalformedException,
            ImageNotSupportedException, ContainerNotFoundException {
        return ResponseEntity.accepted()
                .body(queryService.insert(id, databaseId, tableId, data));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @Transactional
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database. When the location string is set, the data argument is ignored and the location is used as data input")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processable."),
    })
    public ResponseEntity<Integer> importCsv(@NotNull @PathVariable("id") Long id,
                                             @NotNull @PathVariable("databaseId") Long databaseId,
                                             @NotNull @PathVariable("tableId") Long tableId,
                                             @Valid @RequestBody ImportDto data)
            throws TableNotFoundException, DatabaseNotFoundException, TableMalformedException,
            ImageNotSupportedException, ContainerNotFoundException {
        log.info("Insert data from location {} into database id {}", data, databaseId);
        return ResponseEntity.accepted()
                .body(queryService.insert(id, databaseId, tableId, data));
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    @Transactional(readOnly = true)
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
            ImageNotSupportedException, TableMalformedException, PaginationException, ContainerNotFoundException,
            QueryStoreException {
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
        /* fixme query store maybe not created, create it through running findAll() */
        storeService.findAll(id, databaseId);
        final BigInteger count = queryService.count(id, databaseId, tableId, timestamp);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("FDA-COUNT", count.toString());
        final QueryResultDto response = queryService.findAll(id, databaseId, tableId, timestamp, page, size);
        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }


}
