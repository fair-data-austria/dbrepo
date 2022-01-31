package at.tuwien.service.impl;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.QueryService;
import at.tuwien.service.TableService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.time.DateTimeException;
import java.time.Instant;

@Log4j2
@Service
public class QueryServiceImpl extends HibernateConnector implements QueryService {

    private final QueryMapper queryMapper;
    private final TableService tableService;
    private final DatabaseService databaseService;

    @Autowired
    public QueryServiceImpl(QueryMapper queryMapper, TableService tableService, DatabaseService databaseService) {
        this.queryMapper = queryMapper;
        this.tableService = tableService;
        this.databaseService = databaseService;
    }

    @Override
    @Transactional
    public QueryResultDto execute(Long databaseId, Long tableId, ExecuteStatementDto statement)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryMalformedException,
            TableNotFoundException {
        /* validation */
        if (statement.getStatement() == null || statement.getStatement().isBlank()) {
            throw new QueryMalformedException("Query cannot be blank");
        }
        /* find */
        final Table table = tableService.find(databaseId, tableId);
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final long startSession = System.currentTimeMillis();
        final Session session = getSessionFactory(table.getDatabase())
                .openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(statement.getStatement());
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.info("Execution on table id {} affected {} rows", tableId, affectedTuples);
            session.getTransaction()
                    .commit();
        } catch (SQLGrammarException e) {
            throw new QueryMalformedException("Query not valid for this database", e);
        }
        final QueryResultDto result = queryMapper.resultListToQueryResultDto(table, query.getResultList());
        session.close();
        log.debug("query id {}", result.getId());
        log.trace("result {}", result);
        return result;
    }

    @Override
    @Transactional
    public QueryResultDto findAll(@NonNull Long databaseId, @NonNull Long tableId, Instant timestamp, Long page,
                                  Long size) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, TableMalformedException, PaginationException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final Session session = getSessionFactory(database, true)
                .openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        final NativeQuery<?> query = session.createSQLQuery(queryMapper.tableToRawFindAllQuery(table, timestamp, size,
                page));
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.info("Found {} tuples in table id {}", affectedTuples, tableId);
        } catch (PersistenceException e) {
            log.error("Could not find data");
            session.getTransaction()
                    .rollback();
            throw new TableMalformedException("Could not find data", e);
        }
        session.getTransaction()
                .commit();
        final QueryResultDto result;
        try {
            result = queryMapper.queryTableToQueryResultDto(query.getResultList(), table);
        } catch (DateTimeException e) {
            log.error("Failed to parse date from the one stored in the metadata database");
            throw new TableMalformedException("Could not parse date from format", e);
        }
        session.close();
        return result;
    }

    @Override
    @Transactional
    public Integer insert(Long databaseId, Long tableId, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        if (data.getData().size() == 0) return null;
        final long startSession = System.currentTimeMillis();
        final Session session = getSessionFactory(database, true)
                .openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.tableCsvDtoToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        log.trace("query with parameters {}", query.setParameterList(1, raw.getData()));
        return insert(query, session, tableId);
    }

    @Override
    @Transactional
    public Integer insert(Long databaseId, Long tableId, String path) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final Session session = getSessionFactory(database, true)
                .openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.pathToRawInsertQuery(table, path);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        return insert(query, session, tableId);
    }

    /**
     * Executes a insert query on an active Hibernate session on a table with given id and returns the affected rows.
     *
     * @param query   The query.
     * @param session The active Hibernate session.
     * @param tableId The table id.
     * @return The affected rows, if successful.
     * @throws TableMalformedException The table metadata is wrong.
     */
    private Integer insert(NativeQuery<?> query, Session session, Long tableId) throws TableMalformedException {
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.info("Inserted {} tuples on table id {}", affectedTuples, tableId);
        } catch (PersistenceException e) {
            log.error("Could not insert data");
            session.getTransaction()
                    .rollback();
            throw new TableMalformedException("Could not insert data", e);
        }
        session.getTransaction()
                .commit();
        session.close();
        return affectedTuples;
    }

}
