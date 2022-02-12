package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SeederImpl implements Seeder {

    private final Seeder tableSeederImpl;

    @Autowired
    public SeederImpl(Seeder tableSeederImpl) {
        this.tableSeederImpl = tableSeederImpl;
    }

    @Override
    public void seed() {
        tableSeederImpl.seed();
    }
}
