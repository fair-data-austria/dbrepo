package at.tuwien.service.impl;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.querystore.Query;
import at.tuwien.repository.jpa.TableColumnRepository;
import at.tuwien.service.*;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.io.StringReader;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class QueryServiceImpl extends HibernateConnector implements QueryService {

    private final QueryMapper queryMapper;
    private final TableService tableService;
    private final DatabaseService databaseService;
    private final TableColumnRepository tableColumnRepository;
    private final StoreService storeService;

    @Autowired
    public QueryServiceImpl(QueryMapper queryMapper, TableService tableService, DatabaseService databaseService,
                            TableColumnRepository tableColumnRepository, StoreService storeService) {
        this.queryMapper = queryMapper;
        this.tableService = tableService;
        this.databaseService = databaseService;
        this.tableColumnRepository = tableColumnRepository;
        this.storeService = storeService;
    }

    @Override
    @Transactional
    public QueryResultDto execute(Long containerId, Long databaseId, ExecuteStatementDto statement, Long page, Long size)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryMalformedException, QueryStoreException, ContainerNotFoundException, TableNotFoundException, SQLException, JSQLParserException {
        Instant i = Instant.now();
        Query q = storeService.insert(containerId, databaseId, null, statement, i);
        final QueryResultDto result = this.reExecute(containerId,databaseId,q,page,size);
        Long resultNumber = 0L;
        q = storeService.update(containerId,databaseId,result, resultNumber,q);
        return result;
    }

    @Override
    public QueryResultDto reExecute(Long containerId, Long databaseId, Query query, Long page, Long size) throws TableNotFoundException, QueryStoreException, QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException, SQLException, JSQLParserException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database);
        final Session session = factory.openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        Instant i = Instant.now();
        final NativeQuery<?> nativeQuery = session.createSQLQuery(queryMapper.queryToRawTimestampedQuery(query.getQuery(), database, query.getExecution()));
        final int affectedTuples;
        try {
            log.debug("execute raw view-only query {}", query);
            affectedTuples = nativeQuery.executeUpdate();
            log.info("Execution on database id {} affected {} rows", databaseId, affectedTuples);
            session.getTransaction()
                    .commit();
        } catch (SQLGrammarException e) {
            session.close();
            factory.close();
            throw new QueryMalformedException("Query not valid for this database", e);
        }
        /* map the result to the tables (with respective columns) from the statement metadata */
        final List<TableColumn> columns = parseColumns(query, database);
        final QueryResultDto result = queryMapper.resultListToQueryResultDto(columns, nativeQuery.getResultList());
        /* Save query in the querystore */
        session.close();
        factory.close();
        return result;
    }

    @Override
    @Transactional
    public QueryResultDto findAll(Long containerId, Long databaseId, Long tableId, Instant timestamp, Long page,
                                  Long size) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, TableMalformedException, PaginationException,
            ContainerNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        final NativeQuery<?> query = session.createSQLQuery(queryMapper.tableToRawFindAllQuery(table, timestamp, size,
                page));
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.info("Found {} tuples in database id {}", affectedTuples, databaseId);
        } catch (PersistenceException e) {
            log.error("Failed to find data");
            session.close();
            factory.close();
            throw new TableMalformedException("Data not found", e);
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
        factory.close();
        return result;
    }

    @Override
    @Transactional
    public BigInteger count(Long containerId, Long databaseId, Long tableId, Instant timestamp)
            throws DatabaseNotFoundException, TableNotFoundException,
            TableMalformedException, ImageNotSupportedException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, false);
        final Session session = factory.openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        final NativeQuery<BigInteger> query = session.createSQLQuery(queryMapper.tableToRawCountAllQuery(table, timestamp));
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.info("Counted {} tuples in table id {}", affectedTuples, tableId);
        } catch (PersistenceException e) {
            log.error("Failed to count tuples");
            session.close();
            factory.close();
            throw new TableMalformedException("Data not found", e);
        }
        session.getTransaction()
                .commit();
        final BigInteger count = query.getSingleResult();
        session.close();
        factory.close();
        return count;
    }

    @Override
    @Transactional
    public Integer insert(Long containerId, Long databaseId, Long tableId, TableCsvDto data)
            throws ImageNotSupportedException, TableMalformedException, DatabaseNotFoundException,
            TableNotFoundException, ContainerNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        if (data.getData().size() == 0) return null;
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.tableCsvDtoToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        log.trace("query with parameters {}", query.setParameterList(1, raw.getData()));
        return insert(query, session, factory);
    }

    @Override
    @Transactional
    public Integer insert(Long containerId, Long databaseId, Long tableId, ImportDto data)
            throws ImageNotSupportedException, TableMalformedException, DatabaseNotFoundException,
            TableNotFoundException, ContainerNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.debug("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.pathToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        return insert(query, session, factory);
    }

    /**
     * Executes a insert query on an active Hibernate session on a table with given id and returns the affected rows.
     *
     * @param query   The query.
     * @param session The active Hibernate session.
     * @param factory The active Hibernate session factory.
     * @return The affected rows, if successful.
     * @throws TableMalformedException The table metadata is wrong.
     */
    private Integer insert(NativeQuery<?> query, Session session, SessionFactory factory) throws TableMalformedException {
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
        } catch (PersistenceException e) {
            session.close();
            factory.close();
            log.error("Could not insert data: {}", e.getMessage());
            log.throwing(e);
            throw new TableMalformedException("Could not insert data", e);
        }
        session.getTransaction()
                .commit();
        session.close();
        factory.close();
        return affectedTuples;
    }

    /**
     * Retrieves the columns from the tables (ids) and referenced column ids from the metadata database
     *
     * @param statement The list of tables (ids) and referenced column ids.
     * @return The list of columns if successful
     */
    private List<TableColumn> parseColumns(Long databaseId, ExecuteStatementDto statement) {
        final List<TableColumn> columns = new LinkedList<>();
        final int[] idx = new int[]{0};
        log.debug("Database id: {}", databaseId);
        log.debug("ExecuteStatement: {}", statement.toString());
        statement.getTables()
                .forEach(table -> {
                    columns.addAll(statement.getColumns()
                            .get(idx[0]++)
                            .stream()
                            .map(column -> tableColumnRepository
                                    .findByIdAndTidAndCdbid(column.getId(), table.getId(), databaseId))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList()));
                });
        return columns;
    }

    private List<TableColumn> parseColumns(Query query, Database database) throws SQLException, ImageNotSupportedException, JSQLParserException {
        final List<TableColumn> columns = new ArrayList<>();

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
                    if(queryMapper.stringToEscapedString(f.toString()).equals(queryMapper.stringToEscapedString(t.getInternalName()))) {
                        allColumns.addAll(t.getColumns());
                        i=false;
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Table "+queryMapper.stringToEscapedString(f.toString())+ " does not exist");
                }
            }

            //Checking if all columns exist
            for(SelectItem s : selectItems) {
                String select = queryMapper.stringToEscapedString(s.toString());
                log.debug(select);
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
                    if(select.equals(queryMapper.stringToEscapedString(tc.getInternalName()))) {
                        i=false;
                        columns.add(tc);
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Column "+s.toString() + " does not exist");
                }
            }
            return columns;
        }
        else {
            throw new JSQLParserException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }

    }


}
