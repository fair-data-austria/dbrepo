package at.tuwien.endpoints;

import at.tuwien.api.user.UserDto;
import at.tuwien.service.SamlUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.web.bind.annotation.*;

@RestController("/api/auth")
@Log4j2
public class AuthenticationEndpoint {

    private final SamlUserDetailsService userDetailsService;

    @Autowired
    public AuthenticationEndpoint(SamlUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping("/")
    public String index() {
        return "Index";
    }

    @RequestMapping("/failed")
    public ResponseEntity<?> failed() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @GetMapping("/info")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> user(Authentication authentication) {
        final User user = (User) userDetailsService.loadUserBySAML((SAMLCredential) authentication.getCredentials());
        log.debug("is auth {}", user.getUsername());
        return null;
    }

}