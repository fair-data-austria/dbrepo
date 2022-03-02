package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SeederImpl implements Seeder {

    private final Seeder userSeederImpl;

    public SeederImpl(Seeder userSeederImpl) {
        this.userSeederImpl = userSeederImpl;
    }

    @Override
    public void seed() {
        userSeederImpl.seed();
    }
}
