package at.tuwien.service.impl;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.StoreMapper;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.StoreService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.List;

@Log4j2
@Service
public class StoreServiceImpl extends HibernateConnector implements StoreService {

    private final StoreMapper storeMapper;
    private final QueryMapper queryMapper;
    private final DatabaseService databaseService;

    @Autowired
    public StoreServiceImpl(StoreMapper storeMapper, QueryMapper queryMapper, DatabaseService databaseService) {
        this.storeMapper = storeMapper;
        this.queryMapper = queryMapper;
        this.databaseService = databaseService;
    }

    @Override
    @Transactional
    public List<QueryDto> findAll(Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(storeMapper.findAllRawQueryStoreQuery());
        try {
            log.info("Found {} query(s)", query.executeUpdate());
        } catch (PersistenceException e) {
            log.error("Query execution failed");
            throw new QueryStoreException("Failed to execute query", e);
        }
        session.getTransaction()
                .commit();
        final List<QueryDto> queries = queryMapper.resultListToQueryStoreQueryList(query.getResultList());
        session.close();
        return queries;
    }

    @Override
    @Transactional
    public QueryDto findOne(Long databaseId, Long queryId) throws DatabaseNotFoundException, ImageNotSupportedException,
            QueryStoreException, QueryNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(storeMapper.findOneRawQueryStoreQuery());
        query.setParameter(1, queryId);
        try {
            log.info("Found {} query(s)", query.executeUpdate());
        } catch (PersistenceException e) {
            log.error("Query execution failed");
            throw new QueryStoreException("Failed to execute query", e);
        }
        session.getTransaction()
                .commit();
        final List<QueryDto> queries = queryMapper.resultListToQueryStoreQueryList(query.getResultList());
        if (queries.size() != 1) {
            throw new QueryNotFoundException("Query was not found");
        }
        session.close();
        return queries.get(0);
    }


    @Override
    @Transactional
    public void create(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* prepare the statement */
        session.createSQLQuery(storeMapper.createRawQueryStoreSequenceQuery())
                .executeUpdate();
        session.createSQLQuery(storeMapper.createRawQueryStoreQuery())
                .executeUpdate();
        session.getTransaction()
                .commit();
        session.close();
    }

    @Override
    @Transactional
    public void delete(Long databaseId) throws ImageNotSupportedException, DatabaseNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* prepare the statement */
        session.createSQLQuery(storeMapper.deleteRawQueryStoreSequenceQuery())
                .executeUpdate();
        session.createSQLQuery(storeMapper.deleteRawQueryStoreQuery())
                .executeUpdate();
        session.getTransaction()
                .commit();
        session.close();
    }

    @Override
    @Transactional
    public QueryDto insert(Long databaseId, QueryResultDto result, ExecuteQueryDto metadata) throws QueryStoreException,
            DatabaseNotFoundException, ImageNotSupportedException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* execute the statement */
        final QueryDto data = queryMapper.queryResultDtoToQueryDto(result, metadata);
        /* store the result in the query store */
        final NativeQuery<?> query = session.createSQLQuery(storeMapper.insertRawQueryStoreQuery());
        query.setParameterList(1, data.getPreparedValues());
        try {
            log.debug("Inserted {} query(s)", query.executeUpdate());
        } catch (PersistenceException e) {
            log.error("Query execution failed");
            throw new QueryStoreException("Failed to execute query", e);
        }
        session.getTransaction()
                .commit();
        if (query.getResultList().size() != 1) {
            throw new QueryStoreException("Failed to get query result");
        }
        data.setId(((BigInteger) query.getResultList().get(0)).longValue());
        session.close();
        return data;
    }

//
//    private Query parse(QueryDto query, Database database) throws QueryMalformedException {
//        query.setExecutionTimestamp(query.getExecutionTimestamp());
//        Statement statement;
//        final CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
//        try {
//            statement = parserRealSql.parse(new StringReader(query.getQuery()));
//        } catch (JSQLParserException e) {
//            log.error("Could not parse statement");
//            throw new QueryMalformedException("Could not parse statement", e);
//        }
//        log.trace("given query {}", query.getQuery());
//        if (statement instanceof net.sf.jsqlparser.statement.select.Select) {
//            final net.sf.jsqlparser.statement.select.Select selectStatement = (Select) statement;
//            final PlainSelect select = (PlainSelect) selectStatement.getSelectBody();
//            final List<SelectItem> selectItems = select.getSelectItems();
//
//            /* parse all tables */
//            final List<FromItem> items = new ArrayList<>() {{
//                add(select.getFromItem());
//            }};
//            if (select.getJoins() != null && select.getJoins().size() > 0) {
//                for (Join j : select.getJoins()) {
//                    if (j.getRightItem() != null) {
//                        items.add(j.getRightItem());
//                    }
//                }
//            }
//            //Checking if all tables exist
//            List<TableColumn> allColumns = new ArrayList<>();
//            for (FromItem item : items) {
//                boolean error = false;
//                log.debug("from item iterated through: {}", item);
//                for (Table t : database.getTables()) {
//                    if (item.toString().equals(t.getInternalName()) || item.toString().equals(t.getName())) {
//                        allColumns.addAll(t.getColumns());
//                        error = false;
//                        break;
//                    }
//                    error = true;
//                }
//                if (error) {
//                    log.error("Table {} does not exist in remote database", item.toString());
//                    throw new QueryMalformedException("Table does not exist in remote database");
//                }
//            }
//
//            //Checking if all columns exist
//            for (SelectItem s : selectItems) {
//                String manualSelect = s.toString();
//                if (manualSelect.trim().equals("*")) {
//                    log.warn("Please do not use * ('star select') to query data");
//                    continue;
//                }
//                // ignore prefixes
//                if (manualSelect.contains(".")) {
//                    log.debug("manual select {}", manualSelect);
//                    manualSelect = manualSelect.split("\\.")[1];
//                }
//                boolean i = false;
//                for (TableColumn tc : allColumns) {
//                    log.trace("table column {}, {}, {}", tc.getInternalName(), tc.getName(), s);
//                    if (manualSelect.equals(tc.getInternalName()) || manualSelect.toString().equals(tc.getName())) {
//                        i = false;
//                        break;
//                    }
//                    i = true;
//                }
//                if (i) {
//                    log.error("Column {} does not exist", s);
//                    throw new QueryMalformedException("Column does not exist");
//                }
//            }
//            //TODO Future work
//            if (select.getWhere() != null) {
//                Expression where = select.getWhere();
//                log.debug("where clause: {}", where);
//            }
//            return queryMapper.queryDtotoQuery(query);
//        } else {
//            log.error("Provided query is not a select statement, currently we only support 'select' statements");
//            throw new QueryMalformedException("Provided query is not a select statement");
//        }
//
//    }
//

//
//    /**
//     * Re-executes a query with given id in a database by given id, returns a result of size and offset
//     *
//     * @param databaseId The database id
//     * @param queryId    The query id
//     * @param page       The offset
//     * @param size       The size
//     * @return The result set
//     * @throws DatabaseNotFoundException   The database was not found in the metadata database
//     * @throws ImageNotSupportedException  The image is not supported
//     * @throws DatabaseConnectionException The remote container is not available right now
//     * @throws QueryStoreException         There was an error with the query store in the remote container
//     */
//    public QueryResultDto reexecute(Long databaseId, Long queryId, Integer page, Integer size)
//            throws DatabaseNotFoundException, ImageNotSupportedException,
//            QueryStoreException, QueryMalformedException, DatabaseConnectionException {
//        log.info("re-execute query with the id {}", queryId);
//        final DSLContext context;
//        try {
//            context = open(findDatabase(databaseId));
//        } catch (SQLException e) {
//            throw new QueryStoreException("Could not establish connection to query store", e);
//        }
//        final QueryDto savedQuery = findOne(databaseId, queryId);
//        final StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM (");
//        final String q = savedQuery.getQuery();
//        if (q.toLowerCase(Locale.ROOT).contains("where")) {
//            String[] split = q.toLowerCase(Locale.ROOT).split("where");
//            if (split.length > 2) {
//                //TODO FIX SUBQUERIES WITH MULTIPLE Wheres
//                throw new QueryMalformedException("Query Contains Subqueries, this will be supported in a future version");
//            } else {
//                query.append(split[0])
//                        .append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
//                        .append(Timestamp.valueOf(savedQuery.getExecutionTimestamp().toString()))
//                        .append("' WHERE")
//                        .append(split[1])
//                        .append(") as  tab");
//            }
//        } else {
//            query.append(q)
//                    .append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
//                    .append(Timestamp.valueOf(savedQuery.getExecutionTimestamp().toString()))
//                    .append("') as  tab");
//        }
//
//        if (page != null && size != null) {
//            page = Math.abs(page);
//            size = Math.abs(size);
//            query.append(" LIMIT ")
//                    .append(size)
//                    .append(" OFFSET ")
//                    .append(page * size);
//        }
//        query.append(";");
//        final Result<org.jooq.Record> result = context.resultQuery(query.toString())
//                .fetch();
//        log.debug("query string {}", query.toString());
//        log.trace("query result \n {}", result.toString());
//        return queryMapper.recordListToQueryResultDto(result, queryId);
//    }
//


}
