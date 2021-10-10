package at.tuwien.endpoints;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * https://www.baeldung.com/spring-security-saml
 */
@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/auth")
public class AuthenticationEndpoint {

    private final MetadataManager metadataManager;

    @Autowired
    public AuthenticationEndpoint(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    @GetMapping
    @ApiOperation(value = "Check user authentication", notes = "Check if the user is authenticated")
    @ApiResponses({
            @ApiResponse(code = 202, message = "User is authenticated."),
            @ApiResponse(code = 401, message = "The user is not authenticated"),
    })
    public ResponseEntity<?> status() {
        final Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @RequestMapping(value = "/discovery", method = RequestMethod.GET)
    public String idpSelection(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            log.debug("Current authentication instance from security context is null");
        else
            log.debug("Current authentication instance from security context: {}", this.getClass().getSimpleName());
        if (auth == null || (auth instanceof AnonymousAuthenticationToken)) {
            Set<String> idps = metadataManager.getIDPEntityNames();
            for (String idp : idps)
                log.debug("Configured Identity Provider for SSO: {}", idp);
            return "pages/discovery";
        } else {
            log.warn("The current user is already logged.");
            return "redirect:/landing";
        }
    }

}
