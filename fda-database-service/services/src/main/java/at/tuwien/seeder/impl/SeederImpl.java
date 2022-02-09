package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SeederImpl implements Seeder {

    private final Seeder databaseSeederImpl;

    public SeederImpl(Seeder databaseSeederImpl) {
        this.databaseSeederImpl = databaseSeederImpl;
    }

    @Override
    public void seed() {
        databaseSeederImpl.seed();
    }
}
