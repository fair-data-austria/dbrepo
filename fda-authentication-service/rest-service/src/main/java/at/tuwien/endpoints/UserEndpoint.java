package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController("/api/auth")
@CrossOrigin(origins = "*")
public class UserEndpoint {

    @GetMapping
    public String index() {
        return "Index";
    }

}