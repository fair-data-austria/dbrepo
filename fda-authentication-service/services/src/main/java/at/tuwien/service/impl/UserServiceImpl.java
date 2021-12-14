package at.tuwien.service.impl;

import at.tuwien.entities.user.User;
import at.tuwien.exception.UserNotFoundException;
import at.tuwien.repositories.UserRepository;
import at.tuwien.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User findById(Long id) throws UserNotFoundException {
        final Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user with this id");
        }
        return user.get();
    }

    @Override
    @Transactional
    public User findByOid(Long oid) throws UserNotFoundException {
        final Optional<User> user = userRepository.findByOId(oid);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user with this oid");
        }
        return user.get();
    }

    @Override
    @Transactional
    public User save(User user) {
        final User out = userRepository.save(user);
        log.info("Added/updated user with id {}", out.getId());
        return out;
    }
}
