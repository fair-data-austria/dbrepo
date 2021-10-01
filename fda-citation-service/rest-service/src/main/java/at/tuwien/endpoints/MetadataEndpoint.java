package at.tuwien.endpoints;

import at.tuwien.api.zenodo.deposit.DepositChangeRequestDto;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.deposit.DepositResponseDto;
import at.tuwien.exception.*;
import at.tuwien.service.MetadataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/deposit/metadata")
public class MetadataEndpoint {

    private final MetadataService metadataService;

    @Autowired
    public MetadataEndpoint(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping
    public List<DepositResponseDto> findAll(@Valid @RequestParam("id") Long databaseId,
                                            @Valid @RequestParam("tableId") Long tableId) throws ZenodoApiException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        return metadataService.listCitations(databaseId, tableId);
    }

    @PostMapping
    public DepositChangeResponseDto create(@Valid @RequestParam("id") Long databaseId,
                                           @Valid @RequestParam("tableId") Long tableId) throws ZenodoApiException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        return metadataService.storeCitation(databaseId, tableId);
    }

    @PutMapping
    public DepositChangeResponseDto publish(@Valid @RequestParam("id") Long databaseId,
                                            @Valid @RequestParam("tableId") Long tableId) throws ZenodoApiException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            ZenodoNotFoundException {
        return metadataService.publishCitation(databaseId, tableId);
    }

    @GetMapping("/{fileId}")
    public DepositResponseDto find(@Valid @RequestParam("id") Long databaseId,
                                   @Valid @RequestParam("tableId") Long tableId)
            throws MetadataDatabaseNotFoundException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException {
        return metadataService.findCitation(databaseId, tableId);
    }

    @PutMapping("/{fileId}")
    public DepositChangeResponseDto update(@Valid @RequestParam("id") Long databaseId,
                                           @Valid @RequestParam("tableId") Long tableId,
                                           @Valid @RequestBody DepositChangeRequestDto data)
            throws MetadataDatabaseNotFoundException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException {
        return metadataService.updateCitation(databaseId, tableId, data);
    }

    @DeleteMapping("/{fileId}")
    public void delete(@Valid @RequestParam("id") Long databaseId,
                       @Valid @RequestParam("tableId") Long tableId,
                       @NotBlank @RequestParam("fileId") String fileId) throws MetadataDatabaseNotFoundException,
            ZenodoApiException, ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoUnavailableException {
        metadataService.deleteCitation(databaseId, tableId);
    }
}
