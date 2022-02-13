package at.tuwien.service.impl;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public TableServiceImpl(TableRepository tableRepository, DatabaseRepository databaseRepository) {
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
    }

    @Override
    @Transactional
    public Table find(Long databaseId, Long tableId) throws DatabaseNotFoundException, TableNotFoundException {
        final Optional<Database> database = databaseRepository.findById(databaseId);
        if (database.isEmpty()) {
            log.error("Database with id {} not found in metadata database", databaseId);
            throw new DatabaseNotFoundException("Database not found in metadata database");
        }
        final Optional<Table> table = tableRepository.findByDatabaseAndId(database.get(), tableId);
        if (table.isEmpty()) {
            log.error("Table with id {} not found in metadata database", tableId);
            throw new TableNotFoundException("Table not found in metadata database");
        }
        return table.get();
    }
}
