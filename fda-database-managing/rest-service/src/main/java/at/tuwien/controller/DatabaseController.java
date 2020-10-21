package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseDTO;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DatabaseService service;

    @PostMapping(value = "/createDatabase")
    @ApiOperation(value = "creating a new database")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "database created")})
    public Response createDatabase(@RequestBody CreateDatabaseDTO dto) {
        LOGGER.debug("creating new database");
        boolean succeed = service.createDatabase(dto);
        if (succeed) {
            return Response
                    .status(Response.Status.CREATED)
                    .entity("Database container successfully created and started!")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


   /* @PostMapping(value = "/listDatabases")
    public void listDatabases(){


    */


}
