package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.jooq.impl.DSL.constraint;

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
        StringBuilder parsedQuery = new StringBuilder();
        String q = parse(query, database).getQuery();
        if(q.charAt(q.length()-1) == ';') {
            parsedQuery.append(q.substring(0, q.length()-2));
        } else {
            parsedQuery.append(q);
        }
        parsedQuery.append(";");

        ResultQuery<Record> resultQuery = context.resultQuery(parsedQuery.toString());
        Result<Record> result = resultQuery.fetch();
        QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result);
        log.debug("Result of the query is: \n {}", result.toString());

        // Save the query in the store
        boolean b = queryStoreService.saveQuery(database, query, queryResultDto);
        log.debug("Save query returned code {}", b);
        return queryResultDto;
    }

    private Query parse(Query query, Database database) throws SQLException, ImageNotSupportedException, JSQLParserException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        query.setExecutionTimestamp(ts);
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        Statement statement = parserRealSql.parse(new StringReader(query.getQuery()));
        log.debug("Given query {}", query.getQuery());

        if(statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();
            List<SelectItem> selectItems = ps.getSelectItems();

            //Parse all tables
            List<FromItem> fromItems = new ArrayList<>();
            fromItems.add(ps.getFromItem());
            if(ps.getJoins() != null && ps.getJoins().size() > 0) {
                for (Join j : ps.getJoins()) {
                    if (j.getRightItem() != null) {
                        fromItems.add(j.getRightItem());
                    }
                }
            }
            //Checking if all tables exist
            List<TableColumn> allColumns = new ArrayList<>();
            for(FromItem f : fromItems) {
                boolean i = false;
                log.debug("from item iterated through: {}", f);
                for(Table t : database.getTables()) {
                    if(f.toString().equals(t.getInternalName()) || f.toString().equals(t.getName())) {
                        allColumns.addAll(t.getColumns());
                        i=false;
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Table "+f.toString() + " does not exist");
                }
            }

            //Checking if all columns exist
            for(SelectItem s : selectItems) {
                String select = s.toString();
                if(select.trim().equals("*")) {
                    log.debug("Please do not use * to query data");
                    continue;
                }
                // ignore prefixes
                if(select.contains(".")) {
                    log.debug(select);
                    select = select.split("\\.")[1];
                }
                boolean i = false;
                for(TableColumn tc : allColumns ) {
                    log.debug("{},{},{}", tc.getInternalName(), tc.getName(), s);
                    if(select.equals(tc.getInternalName()) || select.toString().equals(tc.getName())) {
                        i=false;
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Column "+s.toString() + " does not exist");
                }
            }
            //TODO Future work
            if(ps.getWhere() != null) {
                Expression where = ps.getWhere();
                log.debug("Where clause: {}", where);
            }
            return query;
        }
        else {
            throw new JSQLParserException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }

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


    public QueryResultDto reexecute(Long databaseId, Long queryId, Integer page, Integer size) throws DatabaseNotFoundException, SQLException, ImageNotSupportedException {
        log.info("re-execute query with the id {}", queryId);
        DSLContext context = open(findDatabase(databaseId));
        QueryResultDto savedQuery = queryStoreService.findOne(databaseId, queryId);
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM (");
        query.append((String)savedQuery.getResult().get(0).get("query"));
        query.append(" FOR SYSTEM_TIME AS OF TIMESTAMP'");
        Timestamp t = (Timestamp)savedQuery.getResult().get(0).get("execution_timestamp");

        query.append(t.toLocalDateTime().toString());
        query.append("') as  tab");

        if(page != null && size != null) {
            page = Math.abs(page);
            size = Math.abs(size);
            query.append(" LIMIT ");
            query.append(size);
            query.append(" OFFSET ");
            query.append(page * size);
        }
        query.append(";");

        log.debug(query.toString());
        ResultQuery<Record> resultQuery = context.resultQuery(query.toString());
        Result<Record> result = resultQuery.fetch();
        QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result);
        log.debug(result.toString());

        return queryResultDto;
    }

}
