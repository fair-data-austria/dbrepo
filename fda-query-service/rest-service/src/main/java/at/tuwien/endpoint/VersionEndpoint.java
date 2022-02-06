package at.tuwien.endpoint;

import at.tuwien.api.database.VersionDto;
import at.tuwien.exception.*;
import at.tuwien.mapper.StoreMapper;
import at.tuwien.querystore.Version;
import at.tuwien.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/version")
public class VersionEndpoint {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @Autowired
    public VersionEndpoint(StoreService storeService, StoreMapper storeMapper) {
        this.storeService = storeService;
        this.storeMapper = storeMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @ApiOperation(value = "Get values", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<List<VersionDto>> getAll(@NotNull @PathVariable("id") Long id,
                                                   @NotNull @PathVariable("databaseId") Long databaseId)
            throws DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {
        final List<Version> versions = storeService.listVersions(id, databaseId);
        return ResponseEntity.ok(versions.stream()
                .map(storeMapper::versionToVersionDto)
                .collect(Collectors.toList()));
    }

}
