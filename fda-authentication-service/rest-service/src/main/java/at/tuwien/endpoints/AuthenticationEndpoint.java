package at.tuwien.endpoints;

import at.tuwien.api.user.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/auth")
public class AuthenticationEndpoint {

    @RequestMapping("/info")
    public ResponseEntity<UserDto> status() {
        log.debug("logged in");
        return ResponseEntity.ok()
                .build();
    }

}