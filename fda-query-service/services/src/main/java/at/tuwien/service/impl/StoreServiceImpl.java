package at.tuwien.service.impl;

import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.query.SaveStatementDto;
import at.tuwien.entities.Query;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.StoreService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Log4j2
@Service
public class StoreServiceImpl extends HibernateConnector implements StoreService {

    private final QueryMapper queryMapper;
    private final DatabaseService databaseService;

    @Autowired
    public StoreServiceImpl(QueryMapper queryMapper, DatabaseService databaseService) {
        this.queryMapper = queryMapper;
        this.databaseService = databaseService;
    }

    @Override
    @Transactional
    public List<Query> findAll(Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* use jpq to select all */
        final org.hibernate.query.Query<Query> queries = session.createQuery("from Query where databaseId = :id", Query.class);
        queries.setParameter("id", databaseId);
        session.getTransaction()
                .commit();
        final List<Query> out = queries.list();
        log.info("Found {} queries", out.size());
        session.close();
        return out;
    }

    @Override
    @Transactional
    public Query findOne(Long databaseId, Long queryId) throws DatabaseNotFoundException, ImageNotSupportedException, QueryNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* use jpa to select one */
        final org.hibernate.query.Query<Query> query = session.createQuery("from Query where databaseId = :dbid and id = :id",
                Query.class);
        query.setParameter("id", queryId);
        query.setParameter("dbid", databaseId);
        session.getTransaction()
                .commit();
        final List<Query> queries = query.list();
        if (queries.size() != 1) {
            log.error("Could not find query with id {}", queryId);
            log.debug("result list is {}", queries.size());
            throw new QueryNotFoundException("Query was not found");
        }
        log.info("Found query with id {}", queryId);
        session.close();
        return queries.get(0);
    }

    @Override
    @Transactional
    public void create(Long databaseId) throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session;
        try {
            session = getSessionFactory(database)
                    .openSession();
        } catch (ServiceException e) {
            log.error("Session opening failed");
            throw new QueryStoreException("session failed", e);
        }
        session.beginTransaction();
        log.info("Created querstore for database id {}", databaseId);
        session.getTransaction()
                .commit();
        session.close();
    }

    @Override
    @Transactional
    public Query insert(Long databaseId, QueryResultDto result, SaveStatementDto metadata)
            throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        return insert(databaseId, result, queryMapper.saveStatementDtoToExecuteStatementDto(metadata));
    }

    @Override
    @Transactional
    public Query insert(Long databaseId, QueryResultDto result, ExecuteStatementDto metadata)
            throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session;
        try {
            session = getSessionFactory(database)
                    .openSession();
        } catch (ServiceException e) {
            log.error("Session opening failed");
            throw new QueryStoreException("session failed", e);
        }
        session.beginTransaction();
        final Query query = Query.builder()
                .databaseId(databaseId)
                .query(metadata.getStatement())
                .queryNormalized(metadata.getStatement())
                .queryHash(DigestUtils.sha256Hex(metadata.getStatement()))
                .resultNumber(Long.parseLong(String.valueOf(result.getResult().size())))
                .resultHash(DigestUtils.sha256Hex(result.getResult().toString()))
                .execution(Instant.now())
                .build();
        /* store the result in the query store */
        final Long id = (Long) session.save(query);
        query.setId(id);
        log.info("Saved query with id {}", id);
        log.debug("saved query {}", query);
        session.getTransaction()
                .commit();
        session.close();
        return query;
    }

}
