package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Slf4j
@Service
public class SeederImpl implements Seeder {

    private final Seeder imageSeederImpl;
    private final Environment environment;
    private final Seeder containerSeederImpl;

    public SeederImpl(Seeder imageSeederImpl, Environment environment, Seeder containerSeederImpl) {
        this.imageSeederImpl = imageSeederImpl;
        this.environment = environment;
        this.containerSeederImpl = containerSeederImpl;
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        imageSeederImpl.seed();
        if (Arrays.asList(environment.getActiveProfiles()).contains("seeder")) {
            containerSeederImpl.seed();
        }
    }
}
