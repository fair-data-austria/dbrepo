package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.jooq.DSLContext;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
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


    @Autowired
    public QueryService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository) {
        super(imageMapper, queryMapper);
        this.databaseRepository = databaseRepository;
    }

    @Transactional
    public List<Query> findAll(Long id) throws ImageNotSupportedException, DatabaseNotFoundException, DatabaseConnectionException, QueryMalformedException {
        return null;
    }

    public QueryResultDto executeStatement(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, JSQLParserException, SQLFeatureNotSupportedException {
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();

        Statement stmt = parserRealSql.parse(new StringReader(query.getQuery()));
        if(stmt instanceof Select) {
            Select selectStatement = (Select) stmt;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();

            List<SelectItem> selectitems = ps.getSelectItems();
            System.out.println(ps.getFromItem().toString());
            selectitems.stream().forEach(selectItem -> System.out.println(selectItem.toString()));
        }
        else {
            throw new SQLFeatureNotSupportedException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }
        //saveQuery(database, query, null);

        return null;
    }

    /**
     * Creates the querystore for a given database
     * @param databaseId
     * @throws ImageNotSupportedException
     * @throws DatabaseNotFoundException
     */
    @Transactional
    public void createQueryStore(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException {
        log.info("Create QueryStore");
        final Database database = findDatabase(databaseId);
        log.info("database {}", database.toString());
        /* create database in container */
        try {
                final DSLContext context = open(database);
                context.createTable("mdb_querystore")
                        .column("id", INTEGER)
                        .column("query", VARCHAR(255).nullable(false))
                        .column("query_hash", VARCHAR(255))
                        .column("execution_timestamp", TIMESTAMP)
                        .column("result_hash", VARCHAR(255))
                        .column("result_number", INTEGER)
                        .constraints(
                                constraint("pk").primaryKey("id")
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

}
