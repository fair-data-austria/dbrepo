package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController("/api/auth")
@Log4j2
public class AuthenticationEndpoint {

    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping(value = "/metadata", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> metadata() throws FileNotFoundException {
        return ResponseEntity.ok(ResourceUtils.getFile("classpath:saml/sp_metadata.xml"));
    }

    @GetMapping("/info")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

}