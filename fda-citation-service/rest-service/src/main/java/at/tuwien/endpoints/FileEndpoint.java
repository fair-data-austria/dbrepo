package at.tuwien.endpoints;

import at.tuwien.service.MetadataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/file")
public class FileEndpoint {

    private final MetadataService citationService;

    @Autowired
    public FileEndpoint(MetadataService citationService) {
        this.citationService = citationService;
    }



}
