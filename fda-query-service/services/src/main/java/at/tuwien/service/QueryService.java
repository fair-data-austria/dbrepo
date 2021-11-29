package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QueryService extends JdbcConnector {

    private final QueryRepository queryRepository;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public QueryService(ImageMapper imageMapper, QueryRepository queryRepository,
                        DatabaseRepository databaseRepository) {
        super(imageMapper);
        this.queryRepository = queryRepository;
        this.databaseRepository = databaseRepository;
    }

    @Deprecated
    @Transactional
    public List<Query> findAll(Long databaseId) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(databaseId);
        if (database.isEmpty()) {
            throw new DatabaseNotFoundException("Database not found in the metadata database");
        }
        final List<Query> queries = database.get()
                .getQueries();
        return queries;
    }

    @Transactional
    public Query findById(Long databaseId, Long queryId) throws QueryNotFoundException {
        final Database database = Database.builder()
                .id(databaseId)
                .build();
        final Optional<Query> query = queryRepository.findByDatabaseAndId(database, queryId);
        if (query.isEmpty()) {
            log.error("Query with id {} was not found to metadata database", queryId);
            throw new QueryNotFoundException("Query was not found to metadata database");
        }
        return query.get();
    }
}