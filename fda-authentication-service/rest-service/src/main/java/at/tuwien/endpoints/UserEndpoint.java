package at.tuwien.endpoints;

import at.tuwien.api.user.UserDto;
import at.tuwien.exception.UserNotFoundException;
import at.tuwien.mapper.UserMapper;
import at.tuwien.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController("/api/auth")
@CrossOrigin(origins = "*")
public class UserEndpoint {

    private final UserMapper userMapper;
    private final UserService userService;

    @Autowired
    public UserEndpoint(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> findAll() {
        final List<UserDto> users = userService.findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) throws UserNotFoundException {
        final UserDto user = userMapper.userToUserDto(userService.findById(id));
        return ResponseEntity.ok(user);
    }

}