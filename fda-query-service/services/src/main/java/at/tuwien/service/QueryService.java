package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.SQLDataType.*;

@Log4j2
@Service
public class QueryService extends JdbcConnector {

    private final DatabaseRepository databaseRepository;
    private final QueryStoreService queryStoreService;
    private final QueryMapper queryMapper;

    @Autowired
    public QueryService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository, QueryStoreService queryStoreService) {
        super(imageMapper, queryMapper);
        this.databaseRepository = databaseRepository;
        this.queryStoreService = queryStoreService;
        this.queryMapper = queryMapper;
    }

    @Transactional
    public QueryResultDto execute(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, JSQLParserException, SQLException, QueryMalformedException, QueryStoreException {
        Database database = findDatabase(id);
        if(database.getContainer().getImage().getDialect().equals("MARIADB")){
            if(!queryStoreService.exists(database)) {
                queryStoreService.create(id);
            }
        }
        DSLContext context = open(database);
        //TODO Fix that import
        ResultQuery<org.jooq.Record> resultQuery = context.resultQuery(parse(query,database).getQuery());
        Result<org.jooq.Record> result = resultQuery.fetch();
        QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result);
        log.debug(result.toString());

        // Save the query in the store
        queryStoreService.saveQuery(database, query, queryResultDto);
        return queryResultDto;
    }

    private Query parse(Query query, Database database) throws SQLException, ImageNotSupportedException {
        DSLContext context = open(database);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        query.setExecutionTimestamp(ts);

        Select<?> select = context.parser().parseSelect(query.getQuery());
        log.debug(select);
        //RETURN columnnames from select
        select.getSelect().stream().forEach(System.out::println);
        String q = select.getSQL();

        return query;

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




}
