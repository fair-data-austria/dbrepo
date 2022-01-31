package at.tuwien.endpoints;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.identifier.IdentifierDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pid")
public class IdentifierEndpoint {

    @GetMapping("/")
    @Transactional
    public List<IdentifierDto> findAll() {
        return null;
    }

    @PostMapping("/")
    @Transactional
    public QueryDto create(@Valid @RequestBody IdentifierDto data) {
        return null;
    }

    @GetMapping("/{pid}")
    @Transactional
    public IdentifierDto find(@Valid @RequestParam("pid") Long persistentId) {
        return null;
    }

    @PutMapping("/{pid}")
    @Transactional
    public IdentifierDto publish(@Valid @RequestParam("pid") Long persistentId) {
        return null;
    }

    @PostMapping("/{pid}")
    @Transactional
    public IdentifierDto update(@Valid @RequestParam("pid") Long persistentId,
                                @Valid @RequestBody IdentifierDto data) {
        return null;
    }

    @DeleteMapping("/{pid}")
    @Transactional
    public void delete(@Valid @RequestParam("pid") Long persistentId,
                       @Valid @PathVariable("queryId") Long queryId) {
    }
}
