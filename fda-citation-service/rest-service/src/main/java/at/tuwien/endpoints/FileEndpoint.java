package at.tuwien.endpoints;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.exception.*;
import at.tuwien.service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/deposit/file")
public class FileEndpoint {

    private final FileService fileService;

    @Autowired
    public FileEndpoint(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<FileResponseDto> listAll(@Valid @RequestParam("id") Long databaseId,
                                         @Valid @RequestParam("tableId") Long tableId)
            throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoApiException,
            ZenodoNotFoundException {
        return fileService.listResources(databaseId, tableId);
    }

    @GetMapping("/{fileId}")
    public FileResponseDto find(@Valid @RequestParam("id") Long databaseId,
                                @Valid @RequestParam("tableId") Long tableId,
                                @NotBlank @RequestParam("fileId") String fileId)
            throws MetadataDatabaseNotFoundException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException {
        return fileService.findResource(databaseId, tableId, fileId);
    }

    @PostMapping
    public FileResponseDto create(@Valid @RequestParam("id") Long databaseId,
                                  @Valid @RequestParam("tableId") Long tableId,
                                  @Valid @RequestParam("data") FileUploadDto data,
                                  @Valid @RequestParam("file") MultipartFile file)
            throws MetadataDatabaseNotFoundException, ZenodoApiException, ZenodoFileTooLargeException,
            ZenodoNotFoundException, ZenodoAuthenticationException {
        return fileService.createResource(databaseId, tableId, data, file);
    }

    @PutMapping("/{fileId}")
    public FileResponseDto update(@Valid @RequestParam("id") Long databaseId,
                                  @Valid @RequestParam("tableId") Long tableId,
                                  @NotBlank @RequestParam("fileId") String fileId) {
        return null;
    }

    @DeleteMapping("/{fileId}")
    public FileResponseDto delete(@Valid @RequestParam("id") Long databaseId,
                                  @Valid @RequestParam("tableId") Long tableId,
                                  @NotBlank @RequestParam("fileId") String fileId) {
        return null;
    }
}
