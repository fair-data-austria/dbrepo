package at.tuwien.endpoints;

import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.query.QueryDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/cite/metadata")
public class MetadataEndpoint {

    @GetMapping
    @Transactional
    public List<QueryDto> findAll(@Valid @PathVariable("id") Long databaseId) {
        return null;
    }

    @PostMapping("/{queryId}")
    @Transactional
    public QueryDto create(@Valid @PathVariable("id") Long databaseId,
                           @Valid @PathVariable("queryId") Long queryId) {
        return null;
    }

    @GetMapping("/{queryId}")
    @Transactional
    public QueryDto find(@Valid @PathVariable("id") Long databaseId,
                         @Valid @RequestParam("queryId") Long queryId) {
        return null;
    }

    @PutMapping("/{queryId}")
    @Transactional
    public QueryDto update(@Valid @PathVariable("id") Long databaseId,
                           @Valid @PathVariable("queryId") Long queryId,
                           @Valid @RequestBody DepositChangeRequestDto data) {
        return null;
    }

    @DeleteMapping("/{queryId}")
    @Transactional
    public void delete(@Valid @PathVariable("id") Long databaseId,
                       @Valid @PathVariable("queryId") Long queryId) {
    }

    @PostMapping("/{queryId}/publish")
    @Transactional
    public QueryDto publish(@Valid @PathVariable("id") Long databaseId,
                            @Valid @PathVariable("queryId") Long queryId) {
        return null;
    }
}
