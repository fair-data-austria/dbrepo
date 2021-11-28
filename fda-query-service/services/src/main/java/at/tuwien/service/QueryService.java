package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class QueryService extends JdbcConnector {

    private final QueryMapper queryMapper;
    private final QueryRepository queryRepository;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public QueryService(ImageMapper imageMapper, QueryMapper queryMapper, QueryRepository queryRepository,
                        DatabaseRepository databaseRepository) {
        super(imageMapper);
        this.queryMapper = queryMapper;
        this.queryRepository = queryRepository;
        this.databaseRepository = databaseRepository;
    }

    @Transactional
    public List<Query> findAll(Long databaseId) throws DatabaseNotFoundException {
        final Database database = findDatabase(databaseId);
        return database.getQueries();
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

    /**
     * Finds the database by id in the metadata database.
     *
     * @param id The database id.
     * @return The database.
     * @throws DatabaseNotFoundException When not found.
     */
    protected Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            throw new DatabaseNotFoundException("Database not found in the metadata database");
        }
        return database.get();
    }
}