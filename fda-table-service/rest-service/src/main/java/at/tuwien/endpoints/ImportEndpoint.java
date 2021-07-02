package at.tuwien.endpoints;

import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.exception.*;
import at.tuwien.service.ImportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}")
public class ImportEndpoint {

    private final ImportService importService;

    @Autowired
    public ImportEndpoint(ImportService importService) {
        this.importService = importService;
    }

    @Transactional
    @PostMapping("/table/{tableId}/import")
    @ApiOperation(value = "Insert values", notes = "Insert Data into a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Updated the table."),
            @ApiResponse(code = 400, message = "The form contains invalid data."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 415, message = "The file provided is not in csv format"),
            @ApiResponse(code = 422, message = "The csv was not processible."),
    })
    public ResponseEntity<?> insert(@PathVariable("id") Long databaseId,
                                    @PathVariable("tableId") Long tableId,
                                    @Valid @ModelAttribute TableInsertDto data) throws TableNotFoundException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException {
        importService.insertFromFile(databaseId, tableId, data);
        return ResponseEntity.accepted()
                .build();
    }

}
