package at.tuwien.endpoints;

import at.tuwien.api.user.JwtResponseDto;
import at.tuwien.api.user.LoginRequestDto;
import at.tuwien.service.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/auth")
public class AuthenticationEndpoint {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationEndpoint(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @ApiOperation(value = "Authenticates a user")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully authenticated a user.")
    })
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto data) {
        final JwtResponseDto response = authenticationService.authenticate(data);
        return ResponseEntity.accepted()
                .body(response);
    }

}