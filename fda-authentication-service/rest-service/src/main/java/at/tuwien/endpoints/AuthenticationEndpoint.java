package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController("/api/auth")
@Log4j2
public class AuthenticationEndpoint {

    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/info")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

}