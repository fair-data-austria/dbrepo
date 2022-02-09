package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeederImpl implements Seeder {

    private final Seeder identifierSeederImpl;

    @Autowired
    public SeederImpl(Seeder identifierSeederImpl) {
        this.identifierSeederImpl = identifierSeederImpl;
    }

    @Override
    @SneakyThrows
    public void seed() {
        Thread.sleep(60 * 1000);
        identifierSeederImpl.seed();
    }
}
