package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.codec.digest.DigestUtils;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.*;

@Log4j2
@Service
public class QueryStoreService extends JdbcConnector {

    public final static String QUERYSTORE_NAME = "userdb_querystore";
    public final static String QUERYSTORE_SEQ_NAME = "seq_querystore_id";

    private final DatabaseRepository databaseRepository;
    private final QueryRepository queryRepository;
    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final QueryMapper queryMapper;

    @Autowired
    public QueryStoreService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository,
                             QueryRepository queryRepository, TableRepository tableRepository, TableMapper tableMapper) {
        super(imageMapper);
        this.databaseRepository = databaseRepository;
        this.queryMapper = queryMapper;
        this.queryRepository = queryRepository;
        this.tableRepository = tableRepository;
        this.tableMapper = tableMapper;
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
                .selectFrom(QUERYSTORE_NAME)
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
        List<org.jooq.Record> records;
        try {
            records = context
                    .selectFrom(QUERYSTORE_NAME)
                    .where(condition("id = " + queryId))
                    .fetch();
        } catch (DataAccessException e) {
            log.error("Failed to select records: {}", e.getMessage());
            throw new QueryStoreException("Failed to select records", e);
        }
        if (records.size() != 1) {
            throw new QueryStoreException("Failed to get query from querystore");
        }
        return queryMapper.recordToQueryDto(records.get(0));
    }

    /**
     * Creates the query store on the remote container for a given database id
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
        if (exists(database)) {
            log.error("Query store already exists for database id {}", databaseId);
            throw new QueryStoreException("Query store already exists");
        }
        /* create database in container */
        try {
            final DSLContext context = open(database);
            context.createSequence(QUERYSTORE_SEQ_NAME)
                    .execute();
            context.createTable(QUERYSTORE_NAME)
                    .column("id", BIGINT)
                    .column("doi", VARCHAR(255).nullable(false))
                    .column("title", VARCHAR(255).nullable(false))
                    .column("query", VARCHAR(255).nullable(false))
                    .column("query_hash", VARCHAR(255))
                    .column("execution_timestamp", TIMESTAMP)
                    .column("result_hash", VARCHAR(255))
                    .column("result_number", BIGINT)
                    .constraints(
                            constraint("pk").primaryKey("id"),
                            constraint("uk").unique("doi")
                    ).execute();
            log.info("Created query store in database id {}", databaseId);
            log.debug("created query store in database {}", database);
        } catch (SQLException e) {
            log.error("Failed to create query store: {}", e.getMessage());
            throw new DataProcessingException("could not create table", e);
        }
    }

    /**
     * Saves a query result for a database
     *
     * @param database    The database.
     * @param query       The query.
     * @param queryResult The query result.
     * @return The query with result.
     * @throws ImageNotSupportedException When not MariaDB.
     * @throws QueryStoreException        When the query store is not found.
     */
    public QueryDto persistQueryResult(Database database, QueryDto query, QueryResultDto queryResult)
            throws ImageNotSupportedException, QueryStoreException {
        // TODO map in mapper next iteration
        query.setExecutionTimestamp(Instant.now());
        query.setQueryNormalized(normalizeQuery(query.getQuery()));
        query.setQueryHash(String.valueOf(query.getQueryNormalized().toLowerCase(Locale.ROOT).hashCode()));
        query.setResultHash(query.getQueryHash());
        query.setResultNumber(0L);
        try {
            final DSLContext context = open(database);
            final BigInteger idVal = nextSequence(database);
            int success = context.insertInto(table(QUERYSTORE_NAME))
                    .columns(field("id"),
                            field("doi"),
                            field("title"),
                            field("query"),
                            field("query_hash"),
                            field("execution_timestamp"),
                            field("result_hash"),
                            field("result_number"))
                    .values(idVal, "doi/" + idVal, query.getTitle(), query.getQuery(),
                            query.getQueryHash(), LocalDateTime.ofInstant(query.getExecutionTimestamp(),
                                    ZoneId.of("Europe/Vienna")), getResultHash(queryResult),
                            queryResult.getResult().size())
                    .execute();
            log.info("Saved query into query store id {}", query.getId());
            log.debug("Saved query into query store {}", query);
            if (success != 1) {
                log.error("Failed to insert record into query store");
                throw new QueryStoreException("Failed to insert record into query store");
            }
        } catch (SQLException e) {
            log.error("The mapped query is not valid: {}", e.getMessage());
            throw new QueryStoreException("The mapped query is not valid", e);
        }
        return query;
    }

    // FIXME mw: lel
    private String normalizeQuery(String query) {
        return query;
    }

    /**
     * Retrieve the result hash
     *
     * @param result The result.
     * @return The hash.
     */
    private String getResultHash(QueryResultDto result) {
        return "sha256:" + DigestUtils.sha256Hex(result.getResult().toString());
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

    /**
     * Executes a query on a database and table.
     *
     * @param databaseId The database.
     * @param tableId    The table.
     * @param data       The query data.
     * @return The query result.
     * @throws ImageNotSupportedException
     * @throws DatabaseNotFoundException
     * @throws QueryStoreException
     * @throws DatabaseConnectionException
     * @throws QueryMalformedException
     * @throws TableNotFoundException
     */
    @Transactional
    public QueryResultDto execute(Long databaseId, Long tableId, ExecuteQueryDto data) throws ImageNotSupportedException,
            DatabaseNotFoundException, QueryStoreException, DatabaseConnectionException, QueryMalformedException, TableNotFoundException {
        final Database database = findDatabase(databaseId);
        final Table table = findTable(database, tableId);
        if (database.getContainer().getImage().getDialect().equals("MARIADB")) {
            if (!exists(database)) {
                create(databaseId);
            }
        }
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            log.error("Failed to connect to the remote database: {}", e.getMessage());
            throw new DatabaseConnectionException("Failed to connect to the remote database", e);
        }
        final QueryDto queryDto = queryMapper.executeQueryDtoToQueryDto(data);
        final QueryResultDto queryResultDto = executeQueryOnContext(context, queryDto, database);
        log.trace("Result of the query is: \n {}", queryResultDto.getResult());

        /* save some metadata */
        final Query metaQuery = queryMapper.queryDtotoQuery(queryDto);
        metaQuery.setExecutionTimestamp(null);
        metaQuery.setTitle(data.getTitle());
        metaQuery.setQdbid(databaseId);
        final Query res = queryRepository.save(metaQuery);
        log.info("Saved executed query in metadata database id {}", res.getId());

        /* save the query in the store */
        final QueryDto out = persistQueryResult(database, queryDto, queryResultDto);
        queryResultDto.setId(out.getId());
        log.info("Saved executed query in query store {}", out.getId());
        log.debug("query store {}", out);
        return queryResultDto;
    }

    private Query parse(QueryDto query, Database database) throws QueryMalformedException {
        query.setExecutionTimestamp(query.getExecutionTimestamp());
        Statement statement;
        final CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        try {
            statement = parserRealSql.parse(new StringReader(query.getQuery()));
        } catch (JSQLParserException e) {
            log.error("Could not parse statement");
            throw new QueryMalformedException("Could not parse statement", e);
        }
        log.trace("given query {}", query.getQuery());
        if (statement instanceof net.sf.jsqlparser.statement.select.Select) {
            final net.sf.jsqlparser.statement.select.Select selectStatement = (Select) statement;
            final PlainSelect select = (PlainSelect) selectStatement.getSelectBody();
            final List<SelectItem> selectItems = select.getSelectItems();

            /* parse all tables */
            final List<FromItem> items = new ArrayList<>() {{
                add(select.getFromItem());
            }};
            if (select.getJoins() != null && select.getJoins().size() > 0) {
                for (Join j : select.getJoins()) {
                    if (j.getRightItem() != null) {
                        items.add(j.getRightItem());
                    }
                }
            }
            //Checking if all tables exist
            List<TableColumn> allColumns = new ArrayList<>();
            for (FromItem item : items) {
                boolean error = false;
                log.debug("from item iterated through: {}", item);
                for (Table t : database.getTables()) {
                    if (item.toString().equals(t.getInternalName()) || item.toString().equals(t.getName())) {
                        allColumns.addAll(t.getColumns());
                        error = false;
                        break;
                    }
                    error = true;
                }
                if (error) {
                    log.error("Table {} does not exist in remote database", item.toString());
                    throw new QueryMalformedException("Table does not exist in remote database");
                }
            }

            //Checking if all columns exist
            for (SelectItem s : selectItems) {
                String manualSelect = s.toString();
                if (manualSelect.trim().equals("*")) {
                    log.warn("Please do not use * ('star select') to query data");
                    continue;
                }
                // ignore prefixes
                if (manualSelect.contains(".")) {
                    log.debug("manual select {}", manualSelect);
                    manualSelect = manualSelect.split("\\.")[1];
                }
                boolean i = false;
                for (TableColumn tc : allColumns) {
                    log.trace("table column {}, {}, {}", tc.getInternalName(), tc.getName(), s);
                    if (manualSelect.equals(tc.getInternalName()) || manualSelect.toString().equals(tc.getName())) {
                        i = false;
                        break;
                    }
                    i = true;
                }
                if (i) {
                    log.error("Column {} does not exist", s);
                    throw new QueryMalformedException("Column does not exist");
                }
            }
            //TODO Future work
            if (select.getWhere() != null) {
                Expression where = select.getWhere();
                log.debug("where clause: {}", where);
            }
            return queryMapper.queryDtotoQuery(query);
        } else {
            log.error("Provided query is not a select statement, currently we only support 'select' statements");
            throw new QueryMalformedException("Provided query is not a select statement");
        }

    }

    /**
     * Saves a query without executing it for a database-table tuple.
     *
     * @param databaseId The database-table tuple.
     * @param tableId    The database-table tuple.
     * @param data       The query data.
     * @return The query entity.
     * @throws ImageNotSupportedException
     * @throws DatabaseNotFoundException
     * @throws QueryStoreException
     * @throws DatabaseConnectionException
     * @throws QueryMalformedException
     */
    @Transactional
    public Query saveWithoutExecution(Long databaseId, Long tableId, ExecuteQueryDto data) throws ImageNotSupportedException,
            DatabaseNotFoundException, QueryStoreException, DatabaseConnectionException,
            QueryMalformedException, TableNotFoundException {
        final Database database = findDatabase(databaseId);
        final Table table = findTable(database, tableId);
        if (database.getContainer().getImage().getDialect().equals("MARIADB")) {
            if (!exists(database)) {
                create(databaseId);
            }
        }
        final QueryDto queryDto = queryMapper.executeQueryDtoToQueryDto(data);
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            throw new QueryMalformedException("Could not connect to the remote container database", e);
        }
        final QueryResultDto queryResultDto = executeQueryOnContext(context, queryDto, database);
        log.trace("Result of the query is: \n {}", queryResultDto.getResult());

        /* save some metadata */
        final Query metaQuery = queryMapper.queryDtotoQuery(queryDto);
        metaQuery.setExecutionTimestamp(null);
        metaQuery.setTitle(data.getTitle());
        metaQuery.setQdbid(databaseId);
        metaQuery.setQtid(tableId);
        metaQuery.setTable(table);
        final Query res = queryRepository.save(metaQuery);
        log.info("Saved query in metadata database id {}", res.getId());

        // Save the query in the store
//        final QueryDto out = saveQuery(database, query, queryResultDto);
//        queryResultDto.setId(out.getId());
//        log.info("Saved query in query store {}", out.getId());
//        log.debug("Save query {}", out);
//        return queryStoreService.findLast(database.getId(), query); // FIXME mw: why query last entry when we set it in the line above?
        return res;
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
        final QueryDto savedQuery = findOne(databaseId, queryId);
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
                        .append(Timestamp.valueOf(savedQuery.getExecutionTimestamp().toString()))
                        .append("' WHERE")
                        .append(split[1])
                        .append(") as  tab");
            }
        } else {
            query.append(q)
                    .append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
                    .append(Timestamp.valueOf(savedQuery.getExecutionTimestamp().toString()))
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
        log.trace("query result \n {}", result.toString());
        return queryMapper.recordListToQueryResultDto(result, queryId);
    }

    /**
     * Find a database in the metadata database by id
     *
     * @param id The id.
     * @return The database.
     * @throws DatabaseNotFoundException The database is not found.
     */
    @Transactional
    protected Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("Database with id {} not found in metadata database", id);
            throw new DatabaseNotFoundException("Database not found in metadata database");
        }
        return database.get();
    }

    /**
     * Find a table in the metadata database by database and id
     *
     * @param database The database.
     * @param id       The id.
     * @return The table.
     * @throws TableNotFoundException The table is not found.
     */
    @Transactional
    protected Table findTable(Database database, Long id) throws TableNotFoundException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(database, id);
        if (table.isEmpty()) {
            log.error("Table with id {} not found in metadata database", id);
            throw new TableNotFoundException("Table not found in metadata database");
        }
        return table.get();
    }

    /**
     * Checks if a database exists in a remote container
     *
     * @param database The database
     * @return True if exists, false otherwise
     * @throws ImageNotSupportedException  The image is not supported
     * @throws DatabaseConnectionException The remote container is not reachable
     */
    protected boolean exists(Database database) throws ImageNotSupportedException, DatabaseConnectionException {
        final DSLContext context;
        try {
            context = open(database);
        } catch (SQLException e) {
            log.error("Could not connect to remote container: {}", e.getMessage());
            throw new DatabaseConnectionException("Could not connect to remote container", e);
        }
        return context.select(count())
                .from("information_schema.tables")
                .where("table_name like '" + QUERYSTORE_NAME + "'")
                .fetchOne(0, int.class) == 1;
    }

    /**
     * Execute query on a remote database context with database metadata to retrieve result
     * mw: did some refactoring for duplicate code
     *
     * @param context  The context
     * @param query    The query
     * @param database The database metadata
     * @return The result
     * @throws QueryMalformedException The query mapping is wrong
     */
    protected QueryResultDto executeQueryOnContext(DSLContext context, QueryDto query, Database database)
            throws QueryMalformedException {
        final StringBuilder parsedQuery = new StringBuilder();
        final String q;
        q = parse(query, database).getQuery();
        if (q.charAt(q.length() - 1) == ';') {
            parsedQuery.append(q.substring(0, q.length() - 2));
        } else {
            parsedQuery.append(q);
        }
        parsedQuery.append(";");
        final List<org.jooq.Record> result = context.resultQuery(parsedQuery.toString())
                .fetch();
        return queryMapper.recordListToQueryResultDto(result, query.getId());
    }

}
