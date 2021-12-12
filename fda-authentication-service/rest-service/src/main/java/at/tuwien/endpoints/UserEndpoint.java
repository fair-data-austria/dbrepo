package at.tuwien.endpoints;

import at.tuwien.api.user.UserDto;
import at.tuwien.exceptions.UserNotFoundException;
import at.tuwien.mapper.UserMapper;
import at.tuwien.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") Long id) throws UserNotFoundException {
        return userMapper.userToUserDto(userService.findById(id));
    }

}