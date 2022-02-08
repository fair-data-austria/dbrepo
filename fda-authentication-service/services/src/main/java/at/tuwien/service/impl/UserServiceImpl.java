package at.tuwien.service.impl;

import at.tuwien.api.auth.SignupRequestDto;
import at.tuwien.entities.user.RoleType;
import at.tuwien.entities.user.User;
import at.tuwien.exception.RoleNotFoundException;
import at.tuwien.exception.UserEmailExistsException;
import at.tuwien.exception.UserNameExistsException;
import at.tuwien.exception.UserNotFoundException;
import at.tuwien.mapper.UserMapper;
import at.tuwien.repositories.UserRepository;
import at.tuwien.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        final List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        return users;
    }

    @Override
    @Transactional
    public User find(Long id) throws UserNotFoundException {
        /* check */
        final Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("User not found with id {}", id);
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }

    @Override
    @Transactional
    public User create(SignupRequestDto data) throws UserEmailExistsException, UserNameExistsException,
            RoleNotFoundException {
        /* check */
        final Optional<User> email = userRepository.findByEmail(data.getEmail());
        if (email.isPresent()) {
            log.error("Email address is already present in the database");
            throw new UserEmailExistsException("Email taken");
        }
        final Optional<User> username = userRepository.findByUsername(data.getEmail());
        if (username.isPresent()) {
            log.error("Username is already present in the database");
            throw new UserNameExistsException("Username taken");
        }
        /* get role */
        /* save */
        final User user = userMapper.signupRequestDtoToUser(data);
        user.setRoles(List.of(RoleType.ROLE_RESEARCHER));
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        final User entity = userRepository.save(user);
        log.info("Created user with id {}", entity.getId());
        log.debug("created user {}", entity);
        return entity;
    }

}
