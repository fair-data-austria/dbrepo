package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.parser.ParserException;

import javax.persistence.EntityNotFoundException;
import java.io.StringReader;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class QueryService {


    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;


    @Autowired
    public QueryService(DatabaseRepository databaseRepository, PostgresService postgresService) {
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
    }

    @Transactional
    public List<Query> findAll(Long id) throws ImageNotSupportedException, DatabaseNotFoundException, DatabaseConnectionException, QueryMalformedException {
        return postgresService.getQueries(findDatabase(id));
    }

    public QueryResultDto executeStatement(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, JSQLParserException, SQLFeatureNotSupportedException {
        Query q = parseQuery(query);
        Database database = findDatabase(id);
        saveQuery(database, query, null);

        return null;
    }

    public void create(Long id) throws DatabaseConnectionException, ImageNotSupportedException, DatabaseNotFoundException {
        Database database = findDatabase(id);
        System.out.println(database);
        postgresService.createQuerystore(database);
    }

    /* helper functions */

    private Database findDatabase(Long id) throws DatabaseNotFoundException, ImageNotSupportedException {
        final Optional<Database> database;
        try {
            database = databaseRepository.findById(id);
        } catch (EntityNotFoundException e) {
            log.error("database not found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database", e);
        }
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        log.debug("retrieved db {}", database.toString());
        if (!database.get().getContainer().getImage().getRepository().equals("postgres")) {
            log.error("Right now only PostgreSQL is supported!");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return database.get();
    }

    private Query saveQuery(Database database, Query query, QueryResultDto queryResult) {
        //TODO in next sprint
        String q = query.getQuery();
        query.setExecutionTimestamp(new Timestamp(System.currentTimeMillis()));
        query.setQueryHash(query.getQueryNormalized().hashCode() + "");
        query.setResultHash(query.getQueryHash());
        query.setResultNumber(0);
        System.out.println(query);
        System.out.println(database.toString());
        postgresService.saveQuery(database, query);
        return null;
    }

    private String normalizeQuery(Select select) {
        return select.getSelectBody().toString();
    }

    private List<SelectItem> normalizeSelectItems(List<SelectItem> selectItems) {
        return selectItems.stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
    }

    private Expression normalizeWhereItems(Expression where) {
        //TODO with AndExpression and OrExpression
        return null;
    }

    private Query parseQuery(Query query) throws JSQLParserException {
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        Statement statement ;
        try {
            statement = parserRealSql.parse(new StringReader(query.getQuery()));
        } catch (JSQLParserException e) {
            log.error(e.getMessage());
            throw e;
        }
        if(statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();
            List<SelectItem> selectItems = ps.getSelectItems();

            //TODO Validate if all columns and tables are in database
            PlainSelect result = new PlainSelect();
            result.setSelectItems(normalizeSelectItems(ps.getSelectItems()));

            result.setFromItem(ps.getFromItem());
            //TODO order join elements?
            //TODO extract from as a whole?
            if(ps.getJoins()!=null) {
                result.setJoins(ps.getJoins());
            }

            if(ps.getWhere() != null) {
                Expression where = ps.getWhere();
                result.setWhere(where);

            }
            selectItems.stream().forEach(selectItem -> System.out.println(selectItem.toString()));
            query.setQueryNormalized(result.toString());
            return query;
        }
        else {
            throw new JSQLParserException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }
    }
}
