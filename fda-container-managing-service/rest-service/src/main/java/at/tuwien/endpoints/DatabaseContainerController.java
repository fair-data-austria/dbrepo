package at.tuwien.endpoints;

import at.tuwien.api.dto.container.ContainerActionTypeDto;
import at.tuwien.api.dto.container.DatabaseContainerBriefDto;
import at.tuwien.api.dto.container.DatabaseContainerDto;
import at.tuwien.api.dto.database.CreateDatabaseContainerDto;
import at.tuwien.api.dto.database.CreateDatabaseResponseDto;
import at.tuwien.entities.DatabaseContainer;
import at.tuwien.mapper.DatabaseContainerMapper;
import at.tuwien.service.ContainerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api")
public class DatabaseContainerController {

    private final ContainerService containerService;
    private final DatabaseContainerMapper databaseContaineMapper;

    @Autowired
    public DatabaseContainerController(ContainerService containerService, DatabaseContainerMapper databaseContaineMapper) {
        this.containerService = containerService;
        this.databaseContaineMapper = databaseContaineMapper;
    }

    @GetMapping("/database")
    @ApiOperation("Get all database containers")
    public ResponseEntity<List<DatabaseContainerBriefDto>> listDatabaseContainers() {
        final List<DatabaseContainer> containers = containerService.getAll();
        return ResponseEntity.ok()
                .body(containers.stream()
                        .map(databaseContaineMapper::databaseContainerToDataBaseContainerBriefDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping("/database")
    @ApiOperation("Create a new database container")
    public ResponseEntity<CreateDatabaseResponseDto> create(@RequestBody CreateDatabaseContainerDto data) {
        final String containerId = containerService.createDatabaseContainer(data);
        log.debug("Create new database {} in container {} with id {}", data.getDatabaseName(), data.getContainerName(), containerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateDatabaseResponseDto.builder()
                        .containerId(containerId)
                        .build());
    }

    @GetMapping("/database/{id}")
    @ApiOperation("Get info of database container")
    public DatabaseContainerDto findById(@RequestParam String id) {
        return databaseContaineMapper.databaseContainerToDataBaseContainerDto(containerService.getDatabaseById(id));

    }

    @PutMapping("/database/{id}")
    @ApiOperation("Update a database container")
    public ResponseEntity<DatabaseContainerBriefDto> change(@RequestParam String id, @RequestBody ContainerActionTypeDto data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/database/{id}")
    @ApiOperation("Delete a database container")
    public ResponseEntity<DatabaseContainerBriefDto> deleteDatabaseContainer(@RequestParam String id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
