package at.tuwien.controller;

import at.tuwien.dto.container.ContainerActionTypeDto;
import at.tuwien.dto.container.ContainerBriefDto;
import at.tuwien.dto.database.CreateDatabaseContainerDto;
import at.tuwien.dto.database.CreateDatabaseResponseDto;
import at.tuwien.model.DatabaseContainer;
import at.tuwien.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
public class DatabaseContainerController {

    private final ContainerService containerService;

    @Autowired
    public DatabaseContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    @GetMapping("/container")
    @ApiOperation("Get all user containers with databases inside it")
    public ResponseEntity<List<ContainerBriefDto>> listDatabaseContainers() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/container")
    @ApiOperation("Create a new user container with database inside it")
    @ApiResponse(message = "database created", code = 201)
    public ResponseEntity<CreateDatabaseResponseDto> create(@RequestBody CreateDatabaseContainerDto data) {
        final String containerId = containerService.createDatabaseContainer(data);
        log.debug("Create new database {} in container {} with id {}", data.getDatabaseName(), data.getContainerName(), containerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateDatabaseResponseDto.builder()
                        .containerId(containerId)
                        .build());
    }

    @GetMapping("/container/{id}")
    @ApiOperation("Get info of user container with database inside it")
    public DatabaseContainer findById(@RequestParam String id) {
        return containerService.getDatabaseContainerByContainerID(id);

    }

    @PostMapping("/container/{id}")
    @ApiOperation("Update a user container with database inside it")
    public ResponseEntity<ContainerBriefDto> change(@RequestParam String id, @RequestBody ContainerActionTypeDto data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/container/{id}")
    @ApiOperation("Delete a user container with database inside it")
    public ResponseEntity<ContainerBriefDto> deleteDatabaseContainer(@RequestParam String id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
