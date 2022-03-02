package at.tuwien.endpoint;

import at.tuwien.exception.*;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Instant;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/table/{tableId}/export")
public class ExportEndpoint {

    private final QueryService queryService;

    @Autowired
    public ExportEndpoint(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Transactional(readOnly = true)
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
            throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException {
        final InputStreamResource resource = queryService.export(id, databaseId, tableId, timestamp);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/csv");
        headers.add("Content-Disposition", "attachment; filename=\"export.csv\"");
        headers.add("Content-Transfer-Encoding", "binary");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


}
