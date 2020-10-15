package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseContainerDTO;
import at.tuwien.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/api")
public class ContainerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerController.class);

    @Autowired
    private ContainerService service;

    @PostMapping(value = "/createDatabaseContainer")
    @ApiOperation(value = "creating a new database container")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "database container created")})
    public Response createDatabaseContainer(@RequestBody CreateDatabaseContainerDTO dto) {
        LOGGER.debug("creating new database container");
        //return "Hi, I am sending the message from fda-container-manging";

        service.createDatabaseContainer(dto);
        return Response
                .status(Response.Status.CREATED)
                .entity("Database container successfully created and started!")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GetMapping(value="/item")
    public String item(){
        return "yes, successful!";
    }

    public void startContainer(){

    }

    public void stopContainer(){

    }

    public void deleteDatabaseContainer(){

    }

    public void getConnectionByDBID(){


    }


}
