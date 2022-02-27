package at.tuwien.service.impl;

import at.tuwien.entities.user.User;
import at.tuwien.exception.UserNotFoundException;
import at.tuwien.repository.jpa.UserRepository;
import at.tuwien.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) throws UserNotFoundException {
        final Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("Failed to find user by username {}", username);
            throw new UserNotFoundException("Failed to find user");
        }
        return user.get();
    }

    @Override
    public User findById(Long id) throws UserNotFoundException {
        final Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("Failed to find user by id {}", id);
            throw new UserNotFoundException("Failed to find user");
        }
        return user.get();
    }
}
