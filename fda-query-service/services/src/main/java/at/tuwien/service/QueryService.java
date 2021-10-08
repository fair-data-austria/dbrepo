package at.tuwien.service;

import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
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

    @Autowired
    public QueryService(ImageMapper imageMapper, QueryRepository queryRepository) {
        super(imageMapper);
        this.queryRepository = queryRepository;
    }

    @Transactional
    public List<Query> findAll(Long databaseId) {
        return queryRepository.findAllByDatabaseId(databaseId);
    }

    @Transactional
    public Query findById(Long queryId) throws QueryNotFoundException {
        final Optional<Query> query = queryRepository.findById(queryId);
        if (query.isEmpty()) {
            log.error("Query with id {} was not found to metadata database", queryId);
            throw new QueryNotFoundException("Query was not found to metadata database");
        }
        return query.get();
    }
}