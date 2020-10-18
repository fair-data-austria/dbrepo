package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseConnectionDataDTO;
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
        String containerId = service.createDatabaseContainer(dto);
        return Response
                .status(Response.Status.CREATED)
                .entity("Database container with containerID: " + containerId + "successfully created and started!")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GetMapping(value = "/getDatabaseConnectionDataByContainerID/{containerID}")
    public CreateDatabaseConnectionDataDTO getDatabaseConnectionDataByContainerID(@PathVariable String containerID) {
        LOGGER.debug("getting data for connection with database");
        return service.getContainerUrlByContainerID(containerID);
      /*  return Response
                .status(Response.Status.FOUND)
                .entity(connectionDataForDB)
                .type(MediaType.APPLICATION_JSON)
                .build();*/

    }

    public void startContainer(String containerID) {
        //TODO
    }

    public void stopContainer(String containerID) {
        //TODO
    }

    public void deleteDatabaseContainer(String containerID) {
        //TODO
    }

}
