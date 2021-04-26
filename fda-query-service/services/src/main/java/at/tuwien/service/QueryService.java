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
import java.sql.SQLSyntaxErrorException;
import java.sql.Timestamp;
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

    public QueryResult executeStatement(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, SQLSyntaxErrorException {
        if(checkValidity(query.getQuery())==false) {
            throw new SQLSyntaxErrorException("SQL Query contains invalid Syntax");
        }
        Database database = findDatabase(id);
        saveQuery(database, query, null);

        return null;
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

    private Query saveQuery(Database database, Query query, QueryResult queryResult) {
        //TODO in next sprint
        String q = query.getQuery();
        query.setExecution_timestamp(new Timestamp(System.currentTimeMillis()));
        query.setQuery_normalized(normalizeQuery(query.getQuery()));
        query.setQuery_hash(query.getQuery_normalized().hashCode()+"");
        query.setResult_hash(query.getQuery_hash());
        query.setResult_number(0);
        postgresService.saveQuery(database, query);
        return null;
    }

    private String normalizeQuery(String query) {
        return query;
    }
    private boolean checkValidity(String query) {
        String queryparts[] = query.toLowerCase().split("from");
        if(queryparts[0].contains("select")) {
            //TODO add more checks
            return true;
        }
        return false;
    }
}
