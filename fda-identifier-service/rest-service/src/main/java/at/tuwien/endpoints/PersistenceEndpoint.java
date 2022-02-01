package at.tuwien.endpoints;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.mapper.IdentifierMapper;
import at.tuwien.service.IdentifierService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pid")
public class PersistenceEndpoint {

    private final IdentifierMapper identifierMapper;
    private final IdentifierService identifierService;

    @Autowired
    public PersistenceEndpoint(IdentifierMapper identifierMapper, IdentifierService identifierService) {
        this.identifierMapper = identifierMapper;
        this.identifierService = identifierService;
    }

    @GetMapping("/{pid}")
    @ApiOperation(value = "Find PID", notes = "Retrieve persistent identifier")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get pid from the metadata database."),
            @ApiResponse(code = 404, message = "TThe pid was not found."),
    })
    public ResponseEntity<IdentifierDto> find(@Valid @PathVariable("pid") Long pid) throws IdentifierNotFoundException {
        return ResponseEntity.ok(identifierMapper.identifierToIdentifierDto(identifierService.find(pid)));
    }

}
