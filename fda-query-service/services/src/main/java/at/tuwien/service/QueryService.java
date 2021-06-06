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
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        Statement stmt;
        try {


            stmt = parserRealSql.parse(new StringReader(query.getQuery()));
        } catch (ParserException e) {
            log.error(e.getMessage());
            throw new SQLFeatureNotSupportedException();
        }
        Query q = parseQuery(stmt);
        Database database = findDatabase(id);
        saveQuery(database, query, null);

        return null;
    }

    public void create(Long id) throws DatabaseConnectionException, ImageNotSupportedException, DatabaseNotFoundException {
        postgresService.createQuerystore(findDatabase(id));
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
        log.debug("retrieved db {}", database);
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
        //query.setQueryNormalized(normalizeQuery(query.getQuery()));
        //query.setQueryHash(query.getQueryNormalized().hashCode() + "");
        //query.setResultHash(query.getQueryHash());
        query.setResultNumber(0);
        postgresService.saveQuery(database, query);
        return null;
    }

    private String normalizeQuery(Select select) {
        return select.getSelectBody().toString();
    }

    private List<SelectItem> normalizeSelectItems(List<SelectItem> selectItems) {
        return selectItems.stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
    }

    private Query parseQuery(Statement statement) throws SQLFeatureNotSupportedException {
        if(statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();

            List<SelectItem> selectItems = ps.getSelectItems();
            FromItem fromItem = ps.getFromItem();
            if(ps.getJoins()!=null) {
            for(Join j : ps.getJoins()) {
                if(j.getRightItem() != null) {
                    System.out.println(j.getRightItem().toString());
                }
            }
            }
            if(ps.getWhere() != null) {
                Expression where = ps.getWhere();
                System.out.println(where.toString());

            }
            selectItems.stream().forEach(selectItem -> System.out.println(selectItem.toString()));
            System.out.println(fromItem.toString());

        }
        else {
            throw new SQLFeatureNotSupportedException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }
        return new Query();
    }
}
