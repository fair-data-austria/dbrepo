package at.tuwien.endpoints;

import at.tuwien.service.CitationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/database/{id}/table/{tableid}/cite")
public class CitationEndpoint {

    private final CitationService citationService;

    @Autowired
    public CitationEndpoint(CitationService citationService) {
        this.citationService = citationService;
    }



}
