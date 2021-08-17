package at.tuwien.service;

import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.elasticsearch.DatabaseElasticsearchRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
public class DatabaseService extends JdbcConnector {

    private final ContainerRepository containerRepository;
    private final DatabaseRepository databaseRepository;
    private final DatabaseElasticsearchRepository databaseElasticsearchRepository;
    private final DatabaseMapper databaseMapper;

    @Autowired
    public DatabaseService(ContainerRepository containerRepository, DatabaseRepository databaseRepository,
                           DatabaseElasticsearchRepository databaseElasticsearchRepository,
                           ImageMapper imageMapper, DatabaseMapper databaseMapper) {
        super(imageMapper, databaseMapper);
        this.containerRepository = containerRepository;
        this.databaseRepository = databaseRepository;
        this.databaseElasticsearchRepository = databaseElasticsearchRepository;
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
            DatabaseMalformedException {
        log.trace("Delete database {}", databaseId);
        final Optional<Database> databaseResponse = databaseRepository.findById(databaseId);
        if (databaseResponse.isEmpty()) {
            log.warn("Database with id {} does not exist", databaseId);
            throw new DatabaseNotFoundException("Database does not exist.");
        }
        try {
            delete(databaseResponse.get());
        } catch (SQLException e) {
            log.error("Could not delete the database: {}", e.getMessage());
            throw new DatabaseMalformedException(e);
        }
        databaseRepository.deleteById(databaseId);
        log.info("Deleted database {}", databaseId);
        log.debug("deleted database {}", databaseResponse.get());
    }

    @Transactional
    public Database create(DatabaseCreateDto createDto) throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException {
        log.trace("Create database {}", createDto);
        final Optional<Container> containerResponse = containerRepository.findById(createDto.getContainerId());
        if (containerResponse.isEmpty()) {
            log.warn("Container with id {} does not exist", createDto.getContainerId());
            throw new ContainerNotFoundException("Container does not exist.");
        }
        // call container to create database
        final Database database = new Database();
        database.setName(createDto.getName());
        database.setInternalName(databaseMapper.databaseToInternalDatabaseName(database));
        database.setContainer(containerResponse.get());
        database.setDescription(createDto.getDescription());
        database.setIsPublic(createDto.getIsPublic());
        try {
            create(database);
        } catch (SQLException e) {
            log.error("Could not create the database: {}", e.getMessage());
            throw new DatabaseMalformedException(e);
        }
        // save in metadata database
        final Database out = databaseRepository.save(database);
        databaseElasticsearchRepository.save(database);
        log.info("Created database {}", out.getId());
        log.debug("created database {}", out);
        return out;
    }

    @Transactional
    public Database modify(DatabaseModifyDto modifyDto) throws ImageNotSupportedException, DatabaseNotFoundException,
            DatabaseMalformedException {
        log.trace("Modify database {}", modifyDto);
        final Optional<Database> databaseResponse = databaseRepository.findById(modifyDto.getDatabaseId());
        if (databaseResponse.isEmpty()) {
            log.warn("Database with id {} does not exist", modifyDto.getDatabaseId());
            throw new DatabaseNotFoundException("Database does not exist.");
        }
        // call container to create database
        final Database database = databaseMapper.modifyDatabaseByDatabaseModifyDto(databaseResponse.get(), modifyDto);
        try {
            modify(databaseResponse.get(), database);
        } catch (SQLException e) {
            log.error("Could not modify the database: {}", e.getMessage());
            throw new DatabaseMalformedException(e);
        }
        // save in metadata database
        final Database out = databaseRepository.save(database);
        log.info("Updated database {}", out.getId());
        log.debug("updated database {}", out);
        return out;
    }

}
