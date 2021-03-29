package at.tuwien.service;

import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
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
            DatabaseMalformedException, ContainerNotFoundException {
        log.debug("get container {}", createDto.getContainerId());
        final Optional<Container> containerResponse = containerRepository.findById(createDto.getContainerId());
        if (containerResponse.isEmpty()) {
            log.error("Container with id {} does not exist", createDto.getContainerId());
            throw new ContainerNotFoundException("Container does not exist.");
        }
        final Container container = containerResponse.get();
        log.debug("retrieved container {}", container);
        // check if postgres
        if (!container.getImage().getRepository().equals("postgres")) {
            log.error("only postgres is supported currently");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported.");
        }
        // save in metadata database
        postgresService.create(container, createDto);
        final Database database = new Database();
        database.setName(createDto.getName());
        database.setContainer(container);
        database.setIsPublic(false);
        final Database out = databaseRepository.save(database);
        log.debug("save db: {}", out);
        log.info("Created a new database '{}' in container {}", createDto.getName(), createDto.getContainerId());
        return out;
    }

}
