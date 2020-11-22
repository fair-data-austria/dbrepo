package at.tuwien.controller;

import at.tuwien.dto.CopyCSVIntoTableDTO;
import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.model.QueryResult;
import at.tuwien.service.QueryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class QueryController {

    private QueryService service;

    @Autowired
    public QueryController(QueryService service) {
        this.service = service;
    }

    @PostMapping("/executeQuery")
    @ApiOperation(value = "executes a query an gives the result as QueryResult object")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Query", response = QueryResult.class)})
    public QueryResult executeQuery(@RequestBody ExecuteQueryDTO dto) {
        return service.executeQuery(dto);
    }


    @PostMapping("/executeStatement")
    @ApiOperation(value = "executes a query an gives the result as response")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "result of Statement", response = Response.class)})
    public Response executeStatement(@RequestBody ExecuteStatementDTO dto) {
        service.executeStatement(dto);

        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PostMapping("/copyCSVIntoTable")
    @ApiOperation(value = "copy CSV Dataset into corresponding table")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "status", response = Response.class)})
    public Response copyCSVIntoTable(@RequestBody CopyCSVIntoTableDTO dto) {
       boolean success = service.copyCSVIntoTable(dto);

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
}
