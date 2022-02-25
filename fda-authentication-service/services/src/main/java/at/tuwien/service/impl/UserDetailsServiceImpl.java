package at.tuwien.service.impl;

import at.tuwien.api.user.UserDetailsDto;
import at.tuwien.entities.user.User;
import at.tuwien.mapper.UserMapper;
import at.tuwien.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("Failed to load user by username {}", username);
            throw new UsernameNotFoundException("Failed to load user by username");
        }
        log.trace("loaded user {}", user);
        final UserDetailsDto details = userMapper.userToUserDetailsDto(user.get());
        details.setAuthorities(user.get()
                .getRoles()
                .stream()
                .map(userMapper::roleTypeToGrantedAuthority)
                .collect(Collectors.toList()));
        log.trace("mapped user {}", details);
        return details;
    }

}
