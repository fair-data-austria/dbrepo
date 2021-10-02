package at.tuwien.service;

import at.tuwien.api.database.query.QueryDto;
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

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    public QueryResultDto execute(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException,
            JSQLParserException, SQLException, QueryStoreException, DatabaseConnectionException {
        final Database database = findDatabase(id);
        if (database.getContainer().getImage().getDialect().equals("MARIADB")) {
            if (!queryStoreService.exists(database)) {
                queryStoreService.create(id);
            }
        }
        final DSLContext context = open(database);
        //TODO Fix that import
        final StringBuilder parsedQuery = new StringBuilder();
        final String q = parse(query, database).getQuery();
        if (q.charAt(q.length() - 1) == ';') {
            parsedQuery.append(q.substring(0, q.length() - 2));
        } else {
            parsedQuery.append(q);
        }
        parsedQuery.append(";");

        final ResultQuery<Record> resultQuery = context.resultQuery(parsedQuery.toString());
        final Result<Record> result = resultQuery.fetch();
        final QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result, query.getId());
        log.debug("Result of the query is: \n {}", result.toString());

        // Save the query in the store
        final Query out = queryStoreService.saveQuery(database, query, queryResultDto);
        queryResultDto.setId(out.getId());
        log.debug("Save query {}", out);
        return queryResultDto;
    }

    private Query parse(Query query, Database database) throws JSQLParserException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        query.setExecutionTimestamp(ts);
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        Statement statement = parserRealSql.parse(new StringReader(query.getQuery()));
        log.debug("Given query {}", query.getQuery());

        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect ps = (PlainSelect) selectStatement.getSelectBody();
            List<SelectItem> selectItems = ps.getSelectItems();

            //Parse all tables
            List<FromItem> fromItems = new ArrayList<>();
            fromItems.add(ps.getFromItem());
            if (ps.getJoins() != null && ps.getJoins().size() > 0) {
                for (Join j : ps.getJoins()) {
                    if (j.getRightItem() != null) {
                        fromItems.add(j.getRightItem());
                    }
                }
            }
            //Checking if all tables exist
            List<TableColumn> allColumns = new ArrayList<>();
            for (FromItem f : fromItems) {
                boolean i = false;
                log.debug("from item iterated through: {}", f);
                for (Table t : database.getTables()) {
                    if (f.toString().equals(t.getInternalName()) || f.toString().equals(t.getName())) {
                        allColumns.addAll(t.getColumns());
                        i = false;
                        break;
                    }
                    i = true;
                }
                if (i) {
                    throw new JSQLParserException("Table " + f.toString() + " does not exist");
                }
            }

            //Checking if all columns exist
            for (SelectItem s : selectItems) {
                String select = s.toString();
                if (select.trim().equals("*")) {
                    log.debug("Please do not use * to query data");
                    continue;
                }
                // ignore prefixes
                if (select.contains(".")) {
                    log.debug(select);
                    select = select.split("\\.")[1];
                }
                boolean i = false;
                for (TableColumn tc : allColumns) {
                    log.debug("{},{},{}", tc.getInternalName(), tc.getName(), s);
                    if (select.equals(tc.getInternalName()) || select.toString().equals(tc.getName())) {
                        i = false;
                        break;
                    }
                    i = true;
                }
                if (i) {
                    throw new JSQLParserException("Column " + s.toString() + " does not exist");
                }
            }
            //TODO Future work
            if (ps.getWhere() != null) {
                Expression where = ps.getWhere();
                log.debug("Where clause: {}", where);
            }
            return query;
        } else {
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

    /**
     * Re-executes a query with given id in a database by given id, returns a result of size and offset
     *
     * @param databaseId The database id
     * @param queryId    The query id
     * @param page       The offset
     * @param size       The size
     * @return The result set
     * @throws DatabaseNotFoundException   The database was not found in the metadata database
     * @throws SQLException                The mapped query is not valid SQL
     * @throws ImageNotSupportedException  The image is not supported
     * @throws DatabaseConnectionException The remote container is not available right now
     * @throws QueryStoreException         There was an error with the query store in the remote container
     */
    public QueryResultDto reexecute(Long databaseId, Long queryId, Integer page, Integer size)
            throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, QueryMalformedException, DatabaseConnectionException {
        log.info("re-execute query with the id {}", queryId);
        final DSLContext context;
        try {
            context = open(findDatabase(databaseId));
        } catch (SQLException e) {
            throw new QueryStoreException("Could not establish connection to query store", e);
        }
        final QueryDto savedQuery = queryStoreService.findOne(databaseId, queryId);
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM (");
        final String q = savedQuery.getQuery();
        if (q.toLowerCase(Locale.ROOT).contains("where")) {
            String[] split = q.toLowerCase(Locale.ROOT).split("where");
            if (split.length > 2) {
                //TODO FIX SUBQUERIES WITH MULTIPLE Wheres
                throw new QueryMalformedException("Query Contains Subqueries, this will be supported in a future version");
            } else {
                query.append(split[0])
                        .append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
                        .append(savedQuery.getExecutionTimestamp().toLocalDateTime().toString())
                        .append("' WHERE")
                        .append(split[1])
                        .append(") as  tab");
            }
        } else {
            query.append(q)
                    .append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
                    .append(savedQuery.getExecutionTimestamp().toLocalDateTime().toString())
                    .append("') as  tab");
        }

        if (page != null && size != null) {
            page = Math.abs(page);
            size = Math.abs(size);
            query.append(" LIMIT ")
                    .append(size)
                    .append(" OFFSET ")
                    .append(page * size);
        }
        query.append(";");
        final Result<org.jooq.Record> result = context.resultQuery(query.toString())
                .fetch();
        log.debug("query string {}", query.toString());
        log.trace("query result {}", result.toString());
        return queryMapper.recordListToQueryResultDto(result, queryId);
    }

    @Transactional
    public QueryResultDto save(Long id, Query query) throws SQLException, ImageNotSupportedException,
            DatabaseNotFoundException, QueryStoreException, JSQLParserException, DatabaseConnectionException {
        Database database = findDatabase(id);
        if (database.getContainer().getImage().getDialect().equals("MARIADB")) {
            if (!queryStoreService.exists(database)) {
                queryStoreService.create(id);
            }
        }
        final DSLContext context = open(database);
        final StringBuilder parsedQuery = new StringBuilder();
        final String q = parse(query, database).getQuery();
        if (q.charAt(q.length() - 1) == ';') {
            parsedQuery.append(q.substring(0, q.length() - 2));
        } else {
            parsedQuery.append(q);
        }
        parsedQuery.append(";");

        final ResultQuery<Record> resultQuery = context.resultQuery(parsedQuery.toString());
        final Result<Record> result = resultQuery.fetch();
        final QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result, query.getId());
        log.debug("Result of the query is: \n {}", result.toString());

        // Save the query in the store
        final Query out = queryStoreService.saveQuery(database, query, queryResultDto);
        queryResultDto.setId(out.getId());
        log.debug("Save query {}", out);
        return queryStoreService.findLast(database.getId(), query);
    }
}
