package at.tuwien.service;

import at.tuwien.dto.container.ContainerDto;
import at.tuwien.dto.database.DatabaseCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.repository.DatabaseRepository;
import exception.ImageNotSupportedException;
import gateway.ContainerGateway;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class DatabaseService {

    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;
    private final ContainerGateway containerGateway;

    @Autowired
    public DatabaseService(DatabaseRepository databaseRepository, PostgresService postgresService,
                           ContainerGateway containerGateway) {
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
        this.containerGateway = containerGateway;
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

    public Database create(DatabaseCreateDto createDto) throws ImageNotSupportedException {
        // get image info for container hash
        final ContainerDto container = containerGateway.inspect(createDto.getContainerId());
        // check if postgres
        if (!container.getImage().getRepository().equals("postgres")) {
            log.error("only postgres is supported currently");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported.");
        }
        // create jdbc statement to create database
        final Container containerRef = new Container();
        containerRef.setId(createDto.getContainerId());
        final Database database = new Database();
        database.setContainer(containerRef);
        database.setName(createDto.getName());
        // save in metadata db
        final Database saved = databaseRepository.save(database);
        log.debug("saved db: {}", saved);
        log.info("Created a new database '{}' in container {}", createDto.getName(), createDto.getContainerId());
        return saved;
    }

}
