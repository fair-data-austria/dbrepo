package at.tuwien.endpoints;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.mapper.FileMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/cite/file")
public class FileEndpoint {

    private final FileMapper fileMapper;

    @Autowired
    public FileEndpoint(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @GetMapping
    public List<FileDto> listAll(@Valid @PathVariable("id") Long databaseId) {
        return null;
    }

    @GetMapping("/{queryId}")
    public FileDto find(@Valid @PathVariable("id") Long databaseId,
                        @Valid @PathVariable("queryId") Long queryId) {
        return null;
    }

    @PostMapping("/{queryId}")
    public FileDto create(@Valid @PathVariable("id") Long databaseId,
                          @Valid @PathVariable("queryId") Long queryId) {
        return null;
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
