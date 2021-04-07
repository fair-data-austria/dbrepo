package at.tuwien.service;

import at.tuwien.dto.CopyCSVIntoTableDTO;
import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.entity.Database;
import at.tuwien.entity.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.entity.QueryResult;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QueryService {


    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;


    @Autowired
    public QueryService(DatabaseRepository databaseRepository, PostgresService postgresService) {
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
    }


    public QueryResult executeQuery(String id, ExecuteQueryDTO dto) {
        System.out.println("test");

        return null;
    }

    public List<Query> findAll(Long id) throws ImageNotSupportedException, DatabaseNotFoundException {
        return postgresService.getQueries(findDatabase(id));
    }

    public boolean executeStatement(ExecuteStatementDTO dto) {
        return false;
    }


    public void create(Long id) throws DatabaseConnectionException, ImageNotSupportedException, DatabaseNotFoundException {
        postgresService.createQuerystore(findDatabase(id));
    }

    private Database findDatabase(Long id) throws DatabaseNotFoundException, ImageNotSupportedException {
        final Optional<Database> database;
        try {
            database = databaseRepository.findById(id);
        } catch (EntityNotFoundException e) {
            log.error("database not found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database", e);
        }
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        log.debug("retrieved db {}", database);
        if (!database.get().getContainer().getImage().getRepository().equals("postgres")) {
            log.error("Right now only PostgreSQL is supported!");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return database.get();
    }
}
