package at.tuwien.seeder.impl;

import at.tuwien.seeder.Seeder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataSeederImpl extends AbstractSeeder implements Seeder {

    @SneakyThrows
    @Override
    public void seed() {
    }

}
