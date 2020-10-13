package at.tuwien.controller;

import at.tuwien.dto.CreateDatabaseContainerDTO;
import at.tuwien.service.ContainerService;
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
@RequestMapping("/api")
public class ContainerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerController.class);

    @Autowired
    private ContainerService service;

    @PostMapping(value = "/databaseContainers")
    @ApiOperation(value = "creating a new database container")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "database container created")})
    public Response createDatabaseContainer(@RequestBody CreateDatabaseContainerDTO dto) {
        LOGGER.debug("creating new database container");
        service.createDatabaseContainer(dto);
        return Response.status(Response.Status.CREATED).build();
    }


}
