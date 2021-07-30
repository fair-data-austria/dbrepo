package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.impl.DSL;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.count;
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
    public QueryResultDto findAll(Long id) throws ImageNotSupportedException, DatabaseNotFoundException, DatabaseConnectionException, QueryMalformedException, SQLException {
        Database database = findDatabase(id);
        DSLContext context = open(database);
        ResultQuery<Record> resultQuery = context.selectQuery();
        Result<Record> result = resultQuery.fetch();
        log.debug(result.toString());
        return queryMapper.recordListToQueryResultDto(context
                .selectFrom(QUERYSTORENAME) //TODO Order after timestamps
                .fetch());
    }

    /**
     * Creates the querystore for a given database
     * @param databaseId
     * @throws ImageNotSupportedException
     * @throws DatabaseNotFoundException
     */
    @Transactional
    public void create(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException, QueryStoreException, SQLException {
        log.info("Create QueryStore");
        final Database database = findDatabase(databaseId);
        if(exists(database)) {
            log.info("Querystore already exists");
            throw new QueryStoreException("Querystore already exists");
        }
        /* create database in container */
        try {
            final DSLContext context = open(database);
            context.createTable(QUERYSTORENAME)
                    .column("id", INTEGER)
                    .column("doi", VARCHAR(255).nullable(false))
                    .column("query", VARCHAR(255).nullable(false))
                    .column("query_hash", VARCHAR(255))
                    .column("execution_timestamp", TIMESTAMP)
                    .column("result_hash", VARCHAR(255))
                    .column("result_number", INTEGER)
                    .constraints(
                            constraint("pk").primaryKey("id"),
                            constraint("uk").unique("doi")
                    )
                    .execute();

        } catch (SQLException e) {
            throw new DataProcessingException("could not create table", e);
        }
    }

    private Query saveQuery(Database database, Query query, QueryResultDto queryResult) {
        //TODO in next sprint
        String q = query.getQuery();
        query.setExecutionTimestamp(new Timestamp(System.currentTimeMillis()));
        query.setQueryNormalized(normalizeQuery(query.getQuery()));
        query.setQueryHash(query.getQueryNormalized().hashCode() + "");
        query.setResultHash(query.getQueryHash());
        query.setResultNumber(0);
        //saveQuery(database, query);
        return null;
    }

    private String normalizeQuery(String query) {
        return query;
    }

    private boolean checkValidity(String query) {
        String queryparts[] = query.toLowerCase().split("from");
        if (queryparts[0].contains("select")) {
            //TODO add more checks
            return true;
        }
        return false;
    }

    @Transactional
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    public boolean exists(Database database) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(database);
        return context.select(count())
                .from("information_schema.tables")
                .where("table_name like '" + QUERYSTORENAME + "'")
                .fetchOne(0, int.class) == 1;
    }

}
