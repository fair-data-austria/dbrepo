package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseContainerDTO;
import at.tuwien.model.DatabaseContainer;
import at.tuwien.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DatabaseContainerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseContainerController.class);

    private ContainerService service;

    @Autowired
    public DatabaseContainerController(ContainerService service) {
        this.service = service;
    }

    @PostMapping("/createDatabaseContainer")
    @ApiOperation("creating a new database container")
    @ApiResponses({@ApiResponse(code = 201, message = "database container created")})
    public Response createDatabaseContainer(@RequestBody CreateDatabaseContainerDTO dto) {
        LOGGER.debug("creating new database container");
        String containerId = service.createDatabaseContainer(dto);
        return Response
                .status(Response.Status.CREATED)
                .entity("Database container with containerID: " + containerId + "successfully created and started!")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GetMapping("/getDatabaseContainerByContainerID")
    public DatabaseContainer getDatabaseContainerByContainerID(@RequestParam String containerID) {
        LOGGER.debug("getting database container by containerID");
        return service.getDatabaseContainerByContainerID(containerID);
      /*  return Response
                .status(Response.Status.FOUND)
                .entity(connectionDataForDB)
                .type(MediaType.APPLICATION_JSON)
                .build();*/

    }

    @GetMapping("/getCreatedDatabaseContainers")
    public List<DatabaseContainer> getCreatedDatabaseContainers() {
        LOGGER.debug("getting created database containers");
        return service.findAllDatabaseContainers();
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
