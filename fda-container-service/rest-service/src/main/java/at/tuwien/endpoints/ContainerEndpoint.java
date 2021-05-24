package at.tuwien.endpoints;

import at.tuwien.api.container.*;
import at.tuwien.api.container.network.IpAddressDto;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ContainerService containerService;
    private final ContainerMapper containerMapper;

    @Autowired
    public ContainerEndpoint(ContainerService containerService, ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
        this.containerService = containerService;
    }

    @GetMapping("/")
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

    @PostMapping("/")
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
    @ApiOperation(value = "Get all informations about a container", notes = "Since we follow the REST-principle, this method provides more information than the findAll method.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get information about container."),
            @ApiResponse(code = 401, message = "Not authorized to get information about a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ContainerDto> findById(@NotNull @PathVariable Long id) throws DockerClientException, ContainerNotFoundException {
        final Container container = containerService.getById(id);
        final ContainerDto containerDto = containerMapper.containerToContainerDto(container);
        try {
            containerService.findIpAddresses(container.getHash())
                    .forEach((key, value) -> containerDto.setIpAddress(IpAddressDto.builder()
                            .ipv4(value)
                            .build()));
        } catch (ContainerNotRunningException e) {
            throw new DockerClientException("Could not get container IP", e);
        }
        final ContainerStateDto stateDto = containerService.getContainerState(container.getHash());
        try {
            containerDto.setState(stateDto);
        } catch (NullPointerException e) {
            throw new DockerClientException("Could not get container state");
        }
        return ResponseEntity.ok()
                .body(containerDto);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Change the state of a container", notes = "The new state can only be one of START/STOP.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Changed the state of a container."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to modify a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ContainerBriefDto> modify(@NotNull @PathVariable Long id, @Valid @RequestBody ContainerChangeDto changeDto)
            throws ContainerNotFoundException, DockerClientException {
        ContainerBriefDto container;
        if (changeDto.getAction().equals(ContainerActionTypeDto.START)) {
            container = containerMapper.containerToDatabaseContainerBriefDto(containerService.start(id));
        } else if (changeDto.getAction().equals(ContainerActionTypeDto.STOP)) {
            container = containerMapper.containerToDatabaseContainerBriefDto(containerService.stop(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(container);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a container")
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
