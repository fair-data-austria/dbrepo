package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationEndpoint {

    @GetMapping("/api/auth")
    public String index() {
        return "Index";
    }

    @GetMapping("/api/auth/info")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

}