package at.tuwien.controller;
import at.tuwien.client.FdaQueryServiceClient;
import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.CSVColumnsResult;
import at.tuwien.model.QueryResult;
import at.tuwien.service.TableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Log4j2
@RestController
@RequestMapping("/api/database")
public class TableController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaQueryServiceClient.class);
    private TableService service;

    @Autowired
    public TableController(TableService service) {
        this.service = service;
    }

    @PostMapping("/{database}/table")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "uploads the CSV file, creates and executes the corresponding SQL commands")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "db-table with CSV dataset created", response = Response.class)})
    public Response create(@PathVariable String database, @RequestParam char delimiter, @RequestParam("file") MultipartFile file) {
        String filePath = service.storeFile(file);
        CreateTableViaCsvDTO dto = new CreateTableViaCsvDTO();
        dto.setContainerID(database);
        dto.setPathToFile(filePath);
        dto.setDelimiter(delimiter);

        boolean success = service.createTableViaCsv(dto);

        if (success) {
            return Response
                    .status(Response.Status.CREATED)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    @GetMapping("/{database}/table")
    @ApiOperation(value = "List all tables of database")
    @ApiResponses({
            @ApiResponse(code = 201, message = "The tables of the database are displayed ", response = Response.class),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database with this id was found")
    })
    public Response findAll(@PathVariable String database) {
        QueryResult result = service.getListOfTablesForContainerID(database);

        if (result != null) {
            return Response
                    .status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(result)
                    .build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GetMapping("/{database}/table/{table}")
    @ApiOperation(value = "List a single table")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Information to the requested table are presented ", response = Response.class),
            @ApiResponse(code = 400, message = "The payload contains invalid data."),
            @ApiResponse(code = 404, message = "No database or no table with this id was found")
    })
    public Response findById(@PathVariable String database,@PathVariable String table) {
        return null;
    }

    @PutMapping("/{database}/table/{table}")
    @ApiOperation(value = "Modify a table (not part of sprint 1) - FIX RETURN TYPES!")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The table was successfully modified."),
            @ApiResponse(code = 400, message = "Parameters were set wrongfully, e.g. more attributes than required."),
            @ApiResponse(code = 401, message = "Not authorized to change a table."),
            @ApiResponse(code = 404, message = "No database with this id was found in metadata database or no table with this id was found in the database."),
    })
    public ResponseEntity<CreateTableViaCsvDTO> modify(@NotBlank @PathVariable String database, @NotBlank @PathVariable String table, @Valid @RequestBody CreateTableViaCsvDTO changeDto) {

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/{database}/table/{table}")
    @ApiOperation(value = "Delete a table")
    @ApiResponses({
            @ApiResponse(code = 202, message = "The table was successfully deleted."),
            @ApiResponse(code = 401, message = "Not authorized to delete a table."),
            @ApiResponse(code = 404, message = "No database with this ID or no table with this ID was found."),
    })
    public ResponseEntity delete(@NotBlank @PathVariable String database, @NotBlank @PathVariable String table) {

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }


}
