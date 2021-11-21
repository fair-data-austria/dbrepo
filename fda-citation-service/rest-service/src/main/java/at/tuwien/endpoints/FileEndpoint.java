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
@RequestMapping("/api/database/{id}/cite/file")
public class FileEndpoint {

    private final FileMapper fileMapper;
    private final FileService fileService;

    @Autowired
    public FileEndpoint(FileMapper fileMapper, FileService fileService) {
        this.fileMapper = fileMapper;
        this.fileService = fileService;
    }

    @GetMapping
    public List<FileDto> listAll(@Valid @PathVariable("id") Long databaseId) {
        return fileService.listResources()
                .stream()
                .map(fileMapper::fileToFileDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{queryId}")
    public FileDto find(@Valid @PathVariable("id") Long databaseId,
                        @Valid @PathVariable("queryId") Long queryId)
            throws RemoteApiException, RemoteNotFoundException, RemoteAuthenticationException,
            RemoteUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {
        return fileMapper.fileToFileDto(fileService.findResource(databaseId, queryId));
    }

    @PostMapping("/{queryId}")
    public FileDto create(@Valid @PathVariable("id") Long databaseId,
                          @Valid @PathVariable("queryId") Long queryId)
            throws RemoteApiException, RemoteNotFoundException, RemoteAuthenticationException,
            RemoteUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException,
            RemoteFileException, MetadataDatabaseNotFoundException {
        return fileMapper.fileToFileDto(fileService.createResource(databaseId, queryId));
    }

    @PutMapping("/{queryId}")
    public FileDto update(@Valid @PathVariable("id") Long databaseId,
                          @Valid @PathVariable("queryId") Long queryId) {
        return null;
    }

    @DeleteMapping("/{queryId}")
    public FileDto delete(@Valid @PathVariable("id") Long databaseId,
                          @Valid @PathVariable("queryId") Long queryId) {
        return null;
    }
}
