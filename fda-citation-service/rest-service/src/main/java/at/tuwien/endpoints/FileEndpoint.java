package at.tuwien.endpoints;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.exception.*;
import at.tuwien.mapper.FileMapper;
import at.tuwien.service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/file")
public class FileEndpoint {

    private final FileMapper fileMapper;
    private final FileService fileService;

    @Autowired
    public FileEndpoint(FileMapper fileMapper, FileService fileService) {
        this.fileMapper = fileMapper;
        this.fileService = fileService;
    }

    @GetMapping
    public List<FileDto> listAll(@Valid @RequestParam("id") Long databaseId,
                                 @Valid @RequestParam("tableId") Long tableId) {
        return fileService.listResources()
                .stream()
                .map(fileMapper::fileToFileDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{fileId}")
    public FileDto find(@Valid @RequestParam("id") Long databaseId,
                        @Valid @RequestParam("tableId") Long tableId,
                        @Valid @RequestParam("queryId") Long queryId)
            throws ZenodoApiException, ZenodoNotFoundException, ZenodoAuthenticationException,
            ZenodoUnavailableException, QueryNotFoundException {
        return fileMapper.fileToFileDto(fileService.findResource(databaseId, tableId, queryId));
    }

    @PostMapping("/{fileId}")
    public FileDto create(@Valid @RequestParam("id") Long databaseId,
                          @Valid @RequestParam("tableId") Long tableId,
                          @Valid @RequestParam("queryId") Long queryId)
            throws ZenodoApiException, ZenodoNotFoundException, ZenodoAuthenticationException,
            ZenodoUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException {
        return fileMapper.fileToFileDto(fileService.createResource(databaseId, tableId, queryId));
    }

    @PutMapping("/{fileId}")
    public FileDto update(@Valid @RequestParam("id") Long databaseId,
                          @Valid @RequestParam("tableId") Long tableId,
                          @Valid @RequestParam("queryId") Long queryId) {
        return null;
    }

    @DeleteMapping("/{fileId}")
    public FileDto delete(@Valid @RequestParam("id") Long databaseId,
                          @Valid @RequestParam("tableId") Long tableId,
                          @Valid @RequestParam("queryId") Long queryId) {
        return null;
    }
}
