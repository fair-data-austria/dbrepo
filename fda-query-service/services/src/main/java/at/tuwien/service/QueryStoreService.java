package at.tuwien.service;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.jooq.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.*;

@Log4j2
@Service
public class QueryStoreService extends JdbcConnector {

    private final DatabaseRepository databaseRepository;
    private final String QUERYSTORENAME = "mdb_querystore";
    private final QueryMapper queryMapper;

    @Autowired
    public QueryStoreService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository) {
        super(imageMapper, queryMapper);
        this.databaseRepository = databaseRepository;
        this.queryMapper = queryMapper;
    }

    @Transactional
    public List<Query> findAll(Long id) throws ImageNotSupportedException, DatabaseNotFoundException,
            DatabaseConnectionException, QueryStoreException {
        final Database database = findDatabase(id);
        if (!exists(database)) {
            create(id);
        }
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to remote container", e);
        }
        log.trace("select query {}", context.selectQuery()
                .fetch()
                .toString());
        return queryMapper.recordListToQueryList(context
                .selectFrom(QUERYSTORENAME)
                .orderBy(field("execution_timestamp"))
                .fetch());
    }

    @Transactional
    public QueryDto findOne(Long databaseId, Long queryId) throws DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, QueryStoreException {
        Database database = findDatabase(databaseId);
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to remote container", e);
        }
        log.trace("select query {}", context.selectQuery()
                .fetch()
                .toString());
        final List<org.jooq.Record> records = context
                .selectFrom(QUERYSTORENAME).where(condition("id = " + queryId))
                .fetch();
        if (records.size() != 1) {
            throw new QueryStoreException("Failed to get query from querystore");
        }
        return queryMapper.recordToQueryDto(records.get(0));
    }

    /**
     * Creates the query store on the remote container for a given datbabase id
     * mw: unfortunately we cannot provide a default sequence nextval for the id
     *
     * @param databaseId The database id
     * @throws ImageNotSupportedException  The image is not supported
     * @throws DatabaseNotFoundException   The database was not found in the metadata database
     * @throws QueryStoreException         Some error with the query store
     * @throws DatabaseConnectionException The connection to the remote container was not able to be established
     */
    @Transactional
    public void create(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException,
            QueryStoreException, DatabaseConnectionException {
        final Database database = findDatabase(databaseId);
        log.info("Create query store in database {}", database);
        if (exists(database)) {
            log.info("Querystore already exists");
            throw new QueryStoreException("Querystore already exists");
        }
        /* create database in container */
        try {
            final DSLContext context = open(database);
            context.createSequence("seq_id")
                    .execute();
            context.createTable(QUERYSTORENAME)
                    .column("id", BIGINT)
                    .column("doi", VARCHAR(255).nullable(false))
                    .column("query", VARCHAR(255).nullable(false))
                    .column("query_hash", VARCHAR(255))
                    .column("execution_timestamp", TIMESTAMP)
                    .column("result_hash", VARCHAR(255))
                    .column("result_number", BIGINT)
                    .constraints(
                            constraint("pk").primaryKey("id"),
                            constraint("uk").unique("doi")
                    )
                    .execute();

        } catch (SQLException e) {
            throw new DataProcessingException("could not create table", e);
        }
    }

    public QueryDto saveQuery(Database database, QueryDto query, QueryResultDto queryResult) throws ImageNotSupportedException, QueryStoreException {
        log.debug("Save query {} into database {}", query, database);
        // TODO map in mapper next iteration
        query.setExecutionTimestamp(Instant.now());
        query.setQueryNormalized(normalizeQuery(query.getQuery()));
        query.setQueryHash(String.valueOf(query.getQueryNormalized().toLowerCase(Locale.ROOT).hashCode()));
        query.setResultHash(query.getQueryHash());
        query.setResultNumber(0L);
        try {
            final DSLContext context = open(database);
            int success = context.insertInto(table(QUERYSTORENAME))
                    .columns(field("id"),
                            field("doi"),
                            field("query"),
                            field("query_hash"),
                            field("execution_timestamp"),
                            field("result_hash"),
                            field("result_number"))
                    .values(sequence("seq_id").nextval(), "doi/" + query.getId(), query.getQuery(),
                            query.getQueryHash(), LocalDateTime.ofInstant(query.getExecutionTimestamp(), ZoneId.of("Europe/Vienna")), "" + queryResult.hashCode(),
                            queryResult.getResult().size())
                    .execute();
            if (success != 1) {
                throw new QueryStoreException("Failed to insert record into query store");
            }
        } catch (SQLException e) {
            throw new QueryStoreException("The mapped query is not valid SQL", e);
        }
        return query;
    }

    // FIXME mw: lel
    private String normalizeQuery(String query) {
        return query;
    }

    @Deprecated
    private boolean checkValidity(String query) {
        String queryparts[] = query.toLowerCase().split("from");
        if (queryparts[0].contains("select")) {
            //TODO add more checks
            return true;
        }
        return false;
    }

    @Transactional
    protected Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    protected boolean exists(Database database) throws ImageNotSupportedException, DatabaseConnectionException {
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to remote container", e);
        }
        return context.select(count())
                .from("information_schema.tables")
                .where("table_name like '" + QUERYSTORENAME + "'")
                .fetchOne(0, int.class) == 1;
    }

}
