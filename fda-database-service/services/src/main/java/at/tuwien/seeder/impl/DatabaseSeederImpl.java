package at.tuwien.seeder.impl;

import at.tuwien.entities.database.Database;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.seeder.Seeder;
import at.tuwien.service.DatabaseService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatabaseSeederImpl extends AbstractSeeder implements Seeder {

    private final DatabaseService databaseService;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public DatabaseSeederImpl(DatabaseService databaseService, DatabaseRepository databaseRepository) {
        this.databaseService = databaseService;
        this.databaseRepository = databaseRepository;
    }

    @SneakyThrows
    @Override
    public void seed() {
        if (databaseRepository.existsById(DATABASE_1_ID)) {
            log.warn("Already seeded. Skip.");
            return;
        }
        final Database database1 = databaseService.create(CONTAINER_1_ID, DATABASE_1_CREATE_DTO);
        log.info("Seeded database id {}", database1.getId());
        final Database database2 = databaseService.create(CONTAINER_2_ID, DATABASE_2_CREATE_DTO);
        log.info("Seeded database id {}", database2.getId());
    }

}
