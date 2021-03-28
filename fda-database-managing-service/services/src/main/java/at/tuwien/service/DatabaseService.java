package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class DatabaseService {

    private final ContainerRepository containerRepository;
    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;

    @Autowired
    public DatabaseService(ContainerRepository containerRepository, DatabaseRepository databaseRepository,
                           PostgresService postgresService) {
        this.containerRepository = containerRepository;
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
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

    public Database create(DatabaseCreateDto createDto) throws ImageNotSupportedException, DatabaseConnectionException,
            DatabaseMalformedException {
        final Container container = containerRepository.getOne(createDto.getContainerId());
        // check if postgres
        if (!container.getImage().getRepository().equals("postgres")) {
            log.error("only postgres is supported currently");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported.");
        }
        // save in metadata database
        final Database database = postgresService.create(container, createDto);
        log.debug("saved db: {}", database);
        log.info("Created a new database '{}' in container {}", createDto.getName(), createDto.getContainerId());
        return database;
    }

}
