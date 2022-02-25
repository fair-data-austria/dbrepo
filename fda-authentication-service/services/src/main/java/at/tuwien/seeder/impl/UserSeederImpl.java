package at.tuwien.seeder.impl;

import at.tuwien.entities.user.User;
import at.tuwien.repositories.UserRepository;
import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Slf4j
@Service
public class UserSeederImpl extends AbstractSeeder implements Seeder {

    private final Environment environment;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserSeederImpl(Environment environment, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.environment = environment;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void seed() {
        if (userRepository.findAll().size() > 0) {
            log.warn("Already seeded. Skip.");
            return;
        }
        USER_1.setPassword(passwordEncoder.encode(USER_1_PASSWORD));
        final User user1 = userRepository.save(USER_1);
        log.info("Created user with id {}", user1);
        if (Arrays.asList(environment.getActiveProfiles()).contains("seeder")) {
            USER_2.setPassword(passwordEncoder.encode(USER_2_PASSWORD));
            final User user2 = userRepository.save(USER_2);
            log.info("Created user with id {}", user2);
            USER_3.setPassword(passwordEncoder.encode(USER_3_PASSWORD));
            final User user3 = userRepository.save(USER_3);
            log.info("Created user with id {}", user3);
            USER_4.setPassword(passwordEncoder.encode(USER_4_PASSWORD));
            final User user4 = userRepository.save(USER_4);
            log.info("Created user with id {}", user4);
        }
    }
}
