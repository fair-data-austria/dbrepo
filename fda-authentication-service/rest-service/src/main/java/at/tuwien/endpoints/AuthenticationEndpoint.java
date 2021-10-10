package at.tuwien.endpoints;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * https://www.baeldung.com/spring-security-saml
 */
@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/auth")
public class AuthenticationEndpoint {


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

}
