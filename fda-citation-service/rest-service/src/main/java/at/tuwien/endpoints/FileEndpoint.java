package at.tuwien.endpoints;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.exception.MetadataDatabaseNotFoundException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/file")
public class FileEndpoint {

    private final FileService fileService;

    @Autowired
    public FileEndpoint(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<FileResponseDto> listAll(@Valid @RequestParam("id") Long databaseId,
                                         @Valid @RequestParam("tableId") Long tableId)
            throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException {
        return fileService.listAll(databaseId, tableId);
    }

//    public FileResponseDto find(@Valid @RequestParam("id") Long databaseId,
//                                @Valid @RequestParam("tableId") Long tableId) {
//
//    }
//
//    public FileResponseDto create(@Valid @RequestParam("id") Long databaseId,
//                                  @Valid @RequestParam("tableId") Long tableId) {
//
//    }
//
//    public FileResponseDto update(@Valid @RequestParam("id") Long databaseId,
//                                  @Valid @RequestParam("tableId") Long tableId) {
//
//    }
//
//    public FileResponseDto delete(@Valid @RequestParam("id") Long databaseId,
//                                  @Valid @RequestParam("tableId") Long tableId) {
//
//    }
}
