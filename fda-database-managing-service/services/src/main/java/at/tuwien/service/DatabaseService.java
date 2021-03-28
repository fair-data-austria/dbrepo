package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.repository.DatabaseRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class DatabaseService {

    private final DatabaseRepository databaseRepository;

    @Autowired
    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public List<Database> findAll() {
        return databaseRepository.findAll();
    }

    public Database findById(Long databaseId) throws DatabaseNotFoundException {
        final Optional<Database> opt = databaseRepository.findById(databaseId);
        if (opt.isEmpty()) {
            log.error("could not find database with id {}", databaseId);
            throw new DatabaseNotFoundException("could not find database with this id");
        }
        return opt.get();
    }

    public void delete(Long databaseId) throws DatabaseNotFoundException {
        final Database database = findById(databaseId);
        databaseRepository.deleteById(databaseId);
    }

    public Database create(DatabaseCreateDto createDto) {
        // get image info for container hash
        // check if postgres
        // create jdbc statement to create database
        // save in metadata db
        return new Database();
    }

}
