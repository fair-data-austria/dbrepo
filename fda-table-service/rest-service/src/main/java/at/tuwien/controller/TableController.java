package at.tuwien.controller;

import at.tuwien.dto.CreateTableViaCsvDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.service.TableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class TableController {

    private TableService service;

    @Autowired
    public TableController(TableService service){
        this.service = service;
    }

    @PostMapping("/createTableViaCSV")
    @ApiOperation(value = "loads a CSV file, creates and executes corresponding SQL commands")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "list of tables", response = Response.class)})
    public Response createTableViaCsv(@RequestBody CreateTableViaCsvDTO dto){
        //method 2 - via Date
       // Date date = new Date();
        //System.out.println("Anfang: "+new Timestamp(date.getTime()));
        boolean success = service.createTableViaCsv(dto);
        //System.out.println(System.currentTimeMillis());
        //date = new Date();
        //System.out.println("Ende: "+new Timestamp(date.getTime()));
        if(success){
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

    @GetMapping("/listTables")
    @ApiOperation(value = "loads a list of created tables in the database")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "list of tables", response = Response.class)})
    public Response listTables(@RequestParam String containerID){
        QueryResult result =service.getListOfTablesForContainerID(containerID);

        if(result != null){
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
