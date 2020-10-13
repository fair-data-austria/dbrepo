package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseDTO;
import at.tuwien.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/database")
public class DatabaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DatabaseService service;

    @PostMapping(value = "/createDatabase")
    @ApiOperation(value = "creating a new database")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "database created")})
    public Response createDatabase(@RequestBody CreateDatabaseDTO dto) {
        LOGGER.debug("creating new database");
        service.createDatabase(dto);
        return Response.status(Response.Status.CREATED).build();
    }

}
