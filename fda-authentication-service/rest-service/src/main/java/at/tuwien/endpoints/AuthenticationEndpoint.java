package at.tuwien.endpoints;

import at.tuwien.api.auth.JwtResponseDto;
import at.tuwien.api.auth.LoginRequestDto;
import at.tuwien.api.user.UserDto;
import at.tuwien.mapper.UserMapper;
import at.tuwien.service.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/auth")
public class AuthenticationEndpoint {

    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationEndpoint(UserMapper userMapper, AuthenticationService authenticationService) {
        this.userMapper = userMapper;
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

    @PutMapping
    @ApiOperation(value = "Authenticates a token")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully authenticated a user.")
    })
    public ResponseEntity<UserDto> authenticateUser(Principal principal) {
        return ResponseEntity.accepted()
                .body(userMapper.principalToUserDto(principal));
    }

}