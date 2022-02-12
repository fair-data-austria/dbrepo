package at.tuwien.seeder.impl;

import at.tuwien.entities.database.table.Table;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.seeder.Seeder;
import at.tuwien.service.TableService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TableSeederImpl extends AbstractSeeder implements Seeder {

    private final TableService tableService;
    private final TableRepository tableRepository;

    @Autowired
    public TableSeederImpl(TableService tableService, TableRepository tableRepository) {
        this.tableService = tableService;
        this.tableRepository = tableRepository;
    }

    @SneakyThrows
    @Override
    public void seed() {
        if (tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID).isPresent()) {
            log.warn("Already seeded. Skip.");
            return;
        }
        final Table table1 = tableService.createTable(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_CREATE_DTO);
        log.info("Seeded table id {}", table1.getId());
        final Table table2 = tableService.createTable(CONTAINER_2_ID, DATABASE_2_ID, TABLE_2_CREATE_DTO);
        log.info("Seeded table id {}", table2.getId());
        final Table table3 = tableService.createTable(CONTAINER_1_ID, DATABASE_1_ID, TABLE_3_CREATE_DTO);
        log.info("Seeded table id {}", table3.getId());
    }

}
