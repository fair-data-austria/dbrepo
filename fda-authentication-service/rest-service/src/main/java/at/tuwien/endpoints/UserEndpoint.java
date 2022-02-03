package at.tuwien.endpoints;

import at.tuwien.api.user.SignupRequestDto;
import at.tuwien.api.user.UserDto;
import at.tuwien.entities.user.User;
import at.tuwien.exception.RoleNotFoundException;
import at.tuwien.exception.UserEmailExistsException;
import at.tuwien.exception.UserNameExistsException;
import at.tuwien.mapper.UserMapper;
import at.tuwien.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/user")
public class UserEndpoint {

    private final UserMapper userMapper;
    private final UserService userService;

    @Autowired
    public UserEndpoint(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "List the users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List the users."),
    })
    @PreAuthorize("hasRole('ROLE_DATA_STEWARD') or hasRole('ROLE_DEVELOPER')")
    public ResponseEntity<List<UserDto>> list() {
        final List<User> users = userService.findAll();
        return ResponseEntity.ok(users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @ApiOperation(value = "Register a new user")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Successfully created a new user."),
            @ApiResponse(code = 400, message = "Invalid payload."),
            @ApiResponse(code = 409, message = "The username is already taken."),
            @ApiResponse(code = 417, message = "The mail is already taken."),
    })
    public ResponseEntity<UserDto> register(@Valid @RequestBody SignupRequestDto data) throws UserEmailExistsException,
            UserNameExistsException, RoleNotFoundException {
        final User user = userService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.userToUserDto(user));
    }

}