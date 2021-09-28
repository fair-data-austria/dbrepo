package at.tuwien.endpoints;

import at.tuwien.service.MetadataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/metadata")
public class MetadataEndpoint {

    private final MetadataService citationService;

    @Autowired
    public MetadataEndpoint(MetadataService citationService) {
        this.citationService = citationService;
    }



}
