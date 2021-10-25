package at.tuwien.endpoints;

import at.tuwien.api.user.UserDto;
import lombok.extern.log4j.Log4j2;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.NameIDImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController("/api/auth")
@Log4j2
public class AuthenticationEndpoint {

    @RequestMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/info")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> user(Principal principal) {
        log.debug("is auth {}", principal.getName());
        return null;
    }

}