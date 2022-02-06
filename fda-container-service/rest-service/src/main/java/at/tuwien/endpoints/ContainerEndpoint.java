package at.tuwien.endpoints;

import at.tuwien.api.container.*;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.service.impl.ContainerServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/container")
public class ContainerEndpoint {

    private final ContainerServiceImpl containerService;
    private final ContainerMapper containerMapper;

    @Autowired
    public ContainerEndpoint(ContainerServiceImpl containerService, ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
        this.containerService = containerService;
    }

    @GetMapping
    @ApiOperation(value = "List all containers", notes = "Lists the containers in the metadata database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All containers are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all containers."),
    })
    public ResponseEntity<List<ContainerBriefDto>> findAll() {
        final List<Container> containers = containerService.getAll();
        return ResponseEntity.ok()
                .body(containers.stream()
                        .map(containerMapper::containerToDatabaseContainerBriefDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "Creates a new container", notes = "Creates a new container whose image is registered in the metadata database too.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created a new container."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to create a container."),
            @ApiResponse(code = 404, message = "The container was not found after creation."),
    })
    public ResponseEntity<ContainerBriefDto> create(@Valid @RequestBody ContainerCreateRequestDto data)
            throws ImageNotFoundException, DockerClientException {
        final Container container = containerService.create(data);
        final ContainerBriefDto response = containerMapper.containerToDatabaseContainerBriefDto(container);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get all information about a container", notes = "Since we follow the REST-principle, this method provides more information than the findAll method.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get information about container."),
            @ApiResponse(code = 401, message = "Not authorized to get information about a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ContainerDto> findById(@NotNull @PathVariable Long id) throws DockerClientException,
            ContainerNotFoundException, ContainerNotRunningException {
        final Container container = containerService.inspect(id);
        return ResponseEntity.ok()
                .body(containerMapper.containerToContainerDto(container));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RESEARCHER')")
    @ApiOperation(value = "Change the state of a container", notes = "The new state can only be one of START/STOP.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Changed the state of a container."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to modify a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ContainerBriefDto> modify(@NotNull @PathVariable Long id,
                                                    @Valid @RequestBody ContainerChangeDto changeDto)
            throws ContainerNotFoundException, DockerClientException {
        final Container container;
        if (changeDto.getAction().equals(ContainerActionTypeDto.START)) {
            container = containerService.start(id);
        } else {
            container = containerService.stop(id);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(containerMapper.containerToDatabaseContainerBriefDto(container));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a container")
    @PreAuthorize("hasRole('ROLE_DATA_STEWARD')")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the container."),
            @ApiResponse(code = 401, message = "Not authorized to delete a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
            @ApiResponse(code = 409, message = "Container is still running."),
    })
    public ResponseEntity<?> delete(@NotNull @PathVariable Long id) throws ContainerNotFoundException,
            DockerClientException, ContainerStillRunningException {
        containerService.remove(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
