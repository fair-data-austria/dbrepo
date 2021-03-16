package at.tuwien.endpoints;

import at.tuwien.api.dto.container.ContainerChangeDto;
import at.tuwien.api.dto.container.DatabaseContainerBriefDto;
import at.tuwien.api.dto.container.DatabaseContainerDto;
import at.tuwien.api.dto.database.DatabaseContainerCreateRequestDto;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
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

import static at.tuwien.api.dto.container.ContainerActionTypeDto.*;

@Log4j2
@RestController
@RequestMapping("/api")
public class DatabaseContainerController {

    private final ContainerService containerService;
    private final DatabaseContainerMapper containerMapper;

    @Autowired
    public DatabaseContainerController(ContainerService containerService, DatabaseContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
        this.containerService = containerService;
    }

    @GetMapping("/container")
    @ApiOperation(value = "List all database containers", notes = "Lists the database containers in the metadata database.")
    public ResponseEntity<List<DatabaseContainerBriefDto>> findAll() {
        final List<DatabaseContainer> containers = containerService.getAll();
        return ResponseEntity.ok()
                .body(containers.stream()
                        .map(containerMapper::databaseContainerToDatabaseContainerBriefDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping("/container")
    @ApiOperation(value = "Creates a new database containers", notes = "Creates a new database container whose image is registered in the metadata database too. Currently for development there is only one image supported 'postgres:12-alpine' as for temporal tables extension requires version 12.")
    public ResponseEntity<DatabaseContainerDto> create(@RequestBody DatabaseContainerCreateRequestDto data)
            throws ImageNotFoundException {
        final DatabaseContainer container = containerService.create(data);
        log.debug("Create new database {} in container {} with id {}", data.getDatabaseName(), data.getContainerName(), container.getContainerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(containerMapper.databaseContainerToDatabaseContainerDto(container));
    }

    @GetMapping("/container/{id}")
    @ApiOperation(value = "Get all informations about a database container", notes = "Since we follow the REST-principle, this method provides more informaiton than the findAll method.")
    public ResponseEntity<DatabaseContainerDto> findById(@RequestParam String id) throws ContainerNotFoundException {
        final DatabaseContainer container = containerService.getById(id);
        return ResponseEntity.ok()
                .body(containerMapper.databaseContainerToDatabaseContainerDto(container));
    }

    @PutMapping("/container/{id}")
    @ApiOperation(value = "Change the state of a database container", notes = "The new state can only be one of START/STOP/REMOVE.", code = 202)
    public ResponseEntity<?> change(@RequestParam String id, @RequestBody ContainerChangeDto changeDto) throws ContainerNotFoundException, DockerClientException {
        if (changeDto.getAction().equals(START) || changeDto.getAction().equals(STOP) || changeDto.getAction().equals(REMOVE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
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

    @DeleteMapping("/container/{id}")
    @ApiOperation(value = "Delete a database container.")
    public ResponseEntity deleteDatabaseContainer(@RequestParam String id) throws ContainerNotFoundException, DockerClientException {
        containerService.remove(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
