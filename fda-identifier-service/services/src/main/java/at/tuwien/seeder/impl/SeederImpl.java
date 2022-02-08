package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("seeder")
@Service
public class SeederImpl implements Seeder {

    private final Seeder databaseSeederImpl;

    public SeederImpl(Seeder databaseSeederImpl) {
        this.databaseSeederImpl = databaseSeederImpl;
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        databaseSeederImpl.seed();
    }
}
