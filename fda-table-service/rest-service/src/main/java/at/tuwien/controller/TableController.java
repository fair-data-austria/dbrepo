package at.tuwien.controller;

import at.tuwien.client.FdaQueryServiceClient;
import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.CSVColumnsResult;
import at.tuwien.model.QueryResult;
import at.tuwien.service.TableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/table")
public class TableController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdaQueryServiceClient.class);
    private TableService service;

    @Autowired
    public TableController(TableService service) {
        this.service = service;
    }

    @PostMapping("/createTableViaCSV")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "uploads the CSV file, creates and executes the corresponding SQL commands")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "db-table with CSV dataset created", response = Response.class)})
    public Response createTableViaCsv(@RequestParam String containerID, @RequestParam char delimiter, @RequestParam("file") MultipartFile file) {
        String filePath = service.storeFile(file);
        CreateTableViaCsvDTO dto = new CreateTableViaCsvDTO();
        dto.setContainerID(containerID);
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

    @PostMapping("/uploadCSVFileAndDetermineDatatypes")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "uploads CSV file, determines datatypes of columns")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "determined datatypes of CSV file", response = Response.class)})
    public Response uploadCSVFileAndDetermineDatatypes(@RequestParam("file") MultipartFile file) {
        CSVColumnsResult csvColumnsResult = service.storeFileAndDetermineDatatypes(file);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(csvColumnsResult)
                .build();
    }

    @GetMapping("/listTables")
    @ApiOperation(value = "loads a list of created tables in the database")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "list of tables", response = Response.class)})
    public Response listTables(@RequestParam String containerID) {
        QueryResult result = service.getListOfTablesForContainerID(containerID);

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


}
