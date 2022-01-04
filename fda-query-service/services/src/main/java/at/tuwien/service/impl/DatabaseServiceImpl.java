package at.tuwien.service.impl;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.service.DatabaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final DatabaseRepository databaseRepository;

    @Autowired
    public DatabaseServiceImpl(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @Override
    @Transactional
    public Database find(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("Database with id {} not found in metadata database", id);
            throw new DatabaseNotFoundException("Database not found in metadata database");
        }
        return database.get();
    }
}
