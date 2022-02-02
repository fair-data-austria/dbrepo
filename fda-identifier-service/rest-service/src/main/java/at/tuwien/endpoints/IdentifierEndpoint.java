package at.tuwien.endpoints;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.*;
import at.tuwien.mapper.IdentifierMapper;
import at.tuwien.service.IdentifierService;
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
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/container/{id}/database/{databaseId}/identifier")
public class IdentifierEndpoint {

    private final IdentifierMapper identifierMapper;
    private final IdentifierService identifierService;

    @Autowired
    public IdentifierEndpoint(IdentifierMapper identifierMapper, IdentifierService identifierService) {
        this.identifierMapper = identifierMapper;
        this.identifierService = identifierService;
    }

    @GetMapping
    @ApiOperation(value = "Find IDs", notes = "Find all identifiers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<List<IdentifierDto>> findAll(@NotNull @PathVariable("id") Long id,
                                                       @NotNull @PathVariable("databaseId") Long databaseId,
                                                       @RequestParam(name = "qid", required = false) Long queryId)
            throws IdentifierNotFoundException {
        if (queryId != null) {
            final Identifier identifier = identifierService.find(id, databaseId, queryId);
            return ResponseEntity.ok(List.of(identifierMapper.identifierToIdentifierDto(identifier)));
        }
        final List<Identifier> identifiers = identifierService.findAll(id, databaseId);
        return ResponseEntity.ok(identifiers.stream()
                .map(identifierMapper::identifierToIdentifierDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @ApiOperation(value = "Create ID", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created the ID."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<IdentifierDto> create(@NotNull @PathVariable("id") Long id,
                                                @NotNull @PathVariable("databaseId") Long databaseId,
                                                @NotNull @Valid @RequestBody IdentifierDto data)
            throws IdentifierAlreadyExistsException, QueryNotFoundException, IdentifierPublishingNotAllowedException,
            RemoteUnavailableException {
        final Identifier identifier = identifierService.create(id, databaseId, data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identifierMapper.identifierToIdentifierDto(identifier));
    }

    @PutMapping("/{identiferId}")
    @ApiOperation(value = "Publish ID", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<?> publish(@NotNull @PathVariable("id") Long id,
                                     @NotNull @PathVariable("databaseId") Long databaseId,
                                     @NotNull @Valid @RequestParam("identiferId") Long persistentId) {
        return null;
    }

    @PostMapping("/{identiferId}")
    @ApiOperation(value = "Update ID", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<IdentifierDto> update(@NotNull @PathVariable("id") Long id,
                                                @NotNull @PathVariable("databaseId") Long databaseId,
                                                @NotNull @Valid @RequestParam("identiferId") Long persistentId,
                                                @NotNull @Valid @RequestBody IdentifierDto data) {
        return null;
    }

    @DeleteMapping("/{identiferId}")
    @ApiOperation(value = "Delete ID", notes = "Get Data from a Table in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get data from the table."),
            @ApiResponse(code = 401, message = "Not authorized to update tables."),
            @ApiResponse(code = 404, message = "The table is not found in database."),
            @ApiResponse(code = 405, message = "The connection to the database was unsuccessful."),
    })
    public ResponseEntity<?> delete(@NotNull @PathVariable("id") Long id,
                                    @NotNull @PathVariable("databaseId") Long databaseId,
                                    @NotNull @Valid @RequestParam("identiferId") Long persistentId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }
}
