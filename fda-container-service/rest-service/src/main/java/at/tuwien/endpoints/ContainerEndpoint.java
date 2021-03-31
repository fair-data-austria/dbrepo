package at.tuwien.endpoints;

import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.container.*;
import at.tuwien.entity.Container;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ContainerStillRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
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

import static at.tuwien.api.dto.container.ContainerActionTypeDto.*;

@Log4j2
@RestController
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(containerMapper.containerToDatabaseContainerBriefDto(container));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get all informations about a container", notes = "Since we follow the REST-principle, this method provides more information than the findAll method.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get information about container."),
            @ApiResponse(code = 401, message = "Not authorized to get information about a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ContainerDto> findById(@NotNull @PathVariable Long id) throws ContainerNotFoundException,
            DockerClientException {
        final Container container = containerService.getById(id);
        final ContainerDto containerDto = containerMapper.containerToContainerDto(container);
        containerService.findIpAddresses(container.getHash())
                .forEach((key, value) -> containerDto.getAddresses().add(IpAddressDto.builder()
                        .network(key)
                        .ipv4(value)
                        .build()));
        final ContainerDto inspectDto = containerMapper.inspectContainerResponseToContainerDto(
                containerService.getContainerState(container.getHash()));
        containerDto.setState(inspectDto.getState());
        return ResponseEntity.ok()
                .body(containerDto);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Change the state of a container", notes = "The new state can only be one of START/STOP/REMOVE.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Changed the state of a container."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to modify a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<?> modify(@NotNull @PathVariable Long id, @Valid @RequestBody ContainerChangeDto changeDto)
            throws ContainerNotFoundException, DockerClientException, ContainerStillRunningException {
        if (changeDto.getAction().equals(START)) {
            containerService.start(id);
        } else if (changeDto.getAction().equals(STOP)) {
            containerService.stop(id);
        } else if (changeDto.getAction().equals(REMOVE)) {
            containerService.remove(id);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a container")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the container."),
            @ApiResponse(code = 401, message = "Not authorized to delete a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
            @ApiResponse(code = 409, message = "Container is still running."),
    })
    public ResponseEntity delete(@NotNull @PathVariable Long id) throws ContainerNotFoundException,
            DockerClientException, ContainerStillRunningException {
        containerService.remove(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
