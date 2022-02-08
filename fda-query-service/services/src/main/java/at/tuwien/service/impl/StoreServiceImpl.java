package at.tuwien.service.impl;

import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.query.SaveStatementDto;
import at.tuwien.entities.container.Container;
import at.tuwien.querystore.Query;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.StoreMapper;
import at.tuwien.service.ContainerService;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.StoreService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Log4j2
@Service
public class StoreServiceImpl extends HibernateConnector implements StoreService {

    private final QueryMapper queryMapper;
    private final StoreMapper storeMapper;
    private final DatabaseService databaseService;
    private final ContainerService containerService;

    @Autowired
    public StoreServiceImpl(QueryMapper queryMapper, StoreMapper storeMapper, DatabaseService databaseService,
                            ContainerService containerService) {
        this.queryMapper = queryMapper;
        this.storeMapper = storeMapper;
        this.databaseService = databaseService;
        this.containerService = containerService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Query> findAll(Long containerId, Long databaseId) throws DatabaseNotFoundException,
            ImageNotSupportedException, QueryStoreException, ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        log.debug("find all queries in database id {}", databaseId);
        /* run query */
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        final Transaction transaction = session.beginTransaction();
        /* use jpq to select all */
        final org.hibernate.query.Query<Query> queries = session.createQuery("select q from Query q", Query.class);
        transaction.commit();
        final List<Query> out = queries.list();
        log.info("Found {} queries", out.size());
        log.debug("found queries {}", out);
        session.close();
        factory.close();
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public Query findOne(Long containerId, Long databaseId, Long queryId) throws DatabaseNotFoundException,
            ImageNotSupportedException, QueryNotFoundException, ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        log.debug("find one query in database id {} with id {}", databaseId, queryId);
        /* run query */
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        final Transaction transaction = session.beginTransaction();
        /* use jpa to select one */
        final org.hibernate.query.Query<Query> query = session.createQuery("from Query where cid = :cid and dbid = :dbid and id = :id",
                Query.class);
        query.setParameter("cid", containerId);
        query.setParameter("dbid", databaseId);
        query.setParameter("id", queryId);
        final Query result = query.uniqueResult();
        transaction.commit();
        if (result == null) {
            log.error("Query not found with id {}", queryId);
            session.close();
            factory.close();
            throw new QueryNotFoundException("Query was not found");
        }
        log.info("Found query with id {}", queryId);
        log.debug("Found query {}", result);
        session.close();
        factory.close();
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Query insert(Long containerId, Long databaseId, QueryResultDto result, SaveStatementDto metadata)
            throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException,
            ContainerNotFoundException {
        return insert(containerId, databaseId, result, queryMapper.saveStatementDtoToExecuteStatementDto(metadata));
    }

    @Override
    @Transactional(readOnly = true)
    public Query insert(Long containerId, Long databaseId, QueryResultDto result, ExecuteStatementDto metadata)
            throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException,
            ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        log.debug("Insert into database id {}, record {}, metadata {}", databaseId, result, metadata);
        /* save */
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        final Transaction transaction = session.beginTransaction();
        final Query query = Query.builder()
                .cid(containerId)
                .dbid(databaseId)
                .query(metadata.getStatement())
                .queryNormalized(metadata.getStatement())
                .queryHash(DigestUtils.sha256Hex(metadata.getStatement()))
                .resultNumber(storeMapper.queryResultDtoToLong(result))
                .resultHash(storeMapper.queryResultDtoToString(result))
                .execution(Instant.now())
                .build();
        session.save(query);
        transaction.commit();
        /* store the result in the query store */
        log.info("Saved query with id {}", query.getId());
        log.debug("saved query {}", query);
        session.close();
        factory.close();
        return query;
    }

}
