package at.tuwien.service.impl;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.QueryService;
import at.tuwien.service.TableService;
import com.github.dockerjava.api.command.AuthCmd;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.time.DateTimeException;
import java.time.Instant;

@Log4j2
@Service
public class QueryServiceImpl extends HibernateConnector implements QueryService {

    private final QueryMapper queryMapper;
    private final TableService tableService;
    private final DatabaseService databaseService;

    @Autowired
    public QueryServiceImpl(QueryMapper queryMapper, TableService tableService, DatabaseService databaseService) {
        this.queryMapper = queryMapper;
        this.tableService = tableService;
        this.databaseService = databaseService;
    }

    @Override
    @Transactional
    public QueryResultDto execute(Long databaseId, Long tableId, QueryDto query) throws TableNotFoundException,
            QueryStoreException, QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto data = ExecuteQueryDto.builder()
                .title(query.getTitle())
                .description(query.getDescription())
                .query(query.getQuery())
                .build();
        final QueryResultDto result = execute(databaseId, tableId, data);
        result.setId(query.getId());
        return result;
    }

    @Override
    @Transactional
    public QueryResultDto execute(Long databaseId, Long tableId, ExecuteQueryDto data)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryMalformedException,
            TableNotFoundException, QueryStoreException {
        /* validation */
        if (data.getTitle() == null || data.getTitle().isBlank()) {
            throw new QueryStoreException("Title cannot be blank");
        }
        if (data.getDescription() == null || data.getDescription().isBlank()) {
            throw new QueryStoreException("Description cannot be blank");
        }
        if (data.getQuery() == null || data.getQuery().isBlank()) {
            throw new QueryMalformedException("Query cannot be blank");
        }
        /* find */
        final Table table = tableService.find(databaseId, tableId);
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(table.getDatabase())
                .openSession();
        session.setDefaultReadOnly(true) /* important */;
        session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(data.getQuery());
        try {
            log.info("Query affected {} rows", query.executeUpdate());
            session.getTransaction()
                    .commit();
        } catch (SQLGrammarException e) {
            throw new QueryMalformedException("Query not valid for this database", e);
        }
        final QueryResultDto result = queryMapper.resultListToQueryResultDto(table, query.getResultList());
        session.close();
        log.debug("Query id {}", result.getId());
        log.trace("result {}", result);
        return result;
    }

    @Override
    @Transactional
    public QueryResultDto findAll(@NonNull Long databaseId, @NonNull Long tableId, Instant timestamp, Long page,
                                  Long size) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DatabaseConnectionException, TableMalformedException, PaginationException {
        if ((page == null && size != null) || (page != null && size == null)) {
            log.error("Cannot perform pagination with only one of page/size set.");
            log.debug("invalid pagination specification, one of page/size is null, either both should be null or none.");
            throw new PaginationException("Invalid pagination parameters");
        }
        if (page != null && page < 0) {
            throw new PaginationException("Page number cannot be lower than 0");
        }
        if (size != null && size <= 0) {
            throw new PaginationException("Page number cannot be lower or equal to 0");
        }
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        final NativeQuery<?> query = session.createSQLQuery(queryMapper.tableToRawFindAllQuery(table, timestamp, size,
                page));
        query.executeUpdate();
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
        return result;
    }

    @Override
    public void insert(Long databaseId, Long tableId, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        if (data.getData().size() == 0 || data.getData().get(0).size() == 0) return;
        final Session session = getSessionFactory(database)
                .openSession();
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.tableTableCsvDtoToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        final int[] idx = {1} /* this needs to be >0 */;
        raw.getValues() /* set values */
                .forEach(row -> query.setParameterList(idx[0]++, row));
        try {
            log.info("Inserted {} tuples", query.executeUpdate());
        } catch (PersistenceException e) {
            log.error("Could not insert data");
            session.getTransaction()
                    .rollback();
            throw new TableMalformedException("Could not insert data", e);
        }
        session.getTransaction()
                .commit();
        session.close();
    }

}
