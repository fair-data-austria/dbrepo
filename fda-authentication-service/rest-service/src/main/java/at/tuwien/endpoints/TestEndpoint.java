package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController("/api/test")
@CrossOrigin(origins = "*")
public class TestEndpoint {

    @GetMapping("/")
    public String index() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("auth {}", auth);
        log.debug("auth principal {}", auth.getPrincipal());
        return "hello";
    }

}