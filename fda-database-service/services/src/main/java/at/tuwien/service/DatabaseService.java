package at.tuwien.service;

import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
public class DatabaseService {

    private final ContainerRepository containerRepository;
    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;
    private final DatabaseMapper databaseMapper;

    @Autowired
    public DatabaseService(ContainerRepository containerRepository, DatabaseRepository databaseRepository,
                           PostgresService postgresService, DatabaseMapper databaseMapper) {
        this.containerRepository = containerRepository;
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
        this.databaseMapper = databaseMapper;
    }

    /**
     * Finds all known databases in the metadata database
     *
     * @return A list of databases
     */
    @Transactional
    public List<Database> findAll() {
        return databaseRepository.findAll();
    }

    /**
     * Finds a database by primary key in the metadata database
     *
     * @param databaseId The key
     * @return The database
     * @throws DatabaseNotFoundException In case the database was not found
     */
    @Transactional
    public Database findById(Long databaseId) throws DatabaseNotFoundException {
        final Optional<Database> opt = databaseRepository.findById(databaseId);
        if (opt.isEmpty()) {
            log.warn("could not find database with id {}", databaseId);
            throw new DatabaseNotFoundException("could not find database with this id");
        }
        return opt.get();
    }

    @Transactional
    public void delete(Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseConnectionException, DatabaseMalformedException {
        final Optional<Database> databaseResponse = databaseRepository.findById(databaseId);
        if (databaseResponse.isEmpty()) {
            log.warn("Database with id {} does not exist", databaseId);
            throw new DatabaseNotFoundException("Database does not exist.");
        }
        final Database database = databaseResponse.get();
        // check if postgres
        if (!database.getContainer().getImage().getRepository().equals("postgres")) {
            log.warn("No support for {}:{}", database.getContainer().getImage().getRepository(), database.getContainer().getImage().getTag());
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported.");
        }
        postgresService.delete(database);
        databaseRepository.deleteById(databaseId);
        log.info("Deleted database {}", databaseId);
        log.debug("deleted database {}", database);
    }

    @Transactional
    public Database create(DatabaseCreateDto createDto) throws ImageNotSupportedException, DatabaseConnectionException,
            DatabaseMalformedException, ContainerNotFoundException {
        final Optional<Container> containerResponse = containerRepository.findById(createDto.getContainerId());
        if (containerResponse.isEmpty()) {
            log.warn("Container with id {} does not exist", createDto.getContainerId());
            throw new ContainerNotFoundException("Container does not exist.");
        }
        final Container container = containerResponse.get();
        // check if postgres
        if (!container.getImage().getRepository().equals("postgres")) {
            log.warn("only postgres is supported currently");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported.");
        }
        // call container to create database
        final Database database = new Database();
        database.setName(createDto.getName());
        database.setContainer(container);
        database.setIsPublic(false);
        database.setInternalName(databaseMapper.databaseToInternalDatabaseName(database));
        postgresService.create(database);
        // save in metadata database
        final Database out = databaseRepository.save(database);
        log.info("Created database {}", out.getId());
        log.debug("created database {}", out);
        return out;
    }

}
