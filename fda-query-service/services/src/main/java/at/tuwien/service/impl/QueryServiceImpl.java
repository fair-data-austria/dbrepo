package at.tuwien.service.impl;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.QueryService;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public QueryResultDto execute(Long databaseId, Long tableId, ExecuteQueryDto data)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryMalformedException, TableNotFoundException {
        /* find */
        final Table table = tableService.find(databaseId, tableId);
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final Session session = getSessionFactory(table.getDatabase())
                .openSession();
        final Transaction transaction = session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(data.getQuery());
        try {
            log.info("Query affected {} rows", query.executeUpdate());
            transaction.commit();
        } catch(SQLGrammarException e) {
            throw new QueryMalformedException("Query not valid for this database", e);
        }
        final QueryResultDto result = queryMapper.resultListToQueryResultDto(table, query.getResultList());
        session.close();
        log.debug("Query id {}", result.getId());
        log.trace("result {}", result);
        return result;
    }

}
