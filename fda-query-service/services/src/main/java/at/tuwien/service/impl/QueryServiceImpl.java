package at.tuwien.service.impl;

import at.tuwien.ExportTableRawQuery;
import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.DataMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.jpa.TableColumnRepository;
import at.tuwien.service.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class QueryServiceImpl extends HibernateConnector implements QueryService {

    private final DataMapper dataMapper;
    private final QueryMapper queryMapper;
    private final TableService tableService;
    private final DatabaseService databaseService;
    private final CommaValueService commaValueService;
    private final TableColumnRepository tableColumnRepository;

    @Autowired
    public QueryServiceImpl(DataMapper dataMapper, QueryMapper queryMapper, TableService tableService,
                            DatabaseService databaseService, CommaValueService commaValueService,
                            TableColumnRepository tableColumnRepository) {
        this.dataMapper = dataMapper;
        this.queryMapper = queryMapper;
        this.tableService = tableService;
        this.databaseService = databaseService;
        this.commaValueService = commaValueService;
        this.tableColumnRepository = tableColumnRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public QueryResultDto execute(Long containerId, Long databaseId, ExecuteStatementDto statement)
            throws DatabaseNotFoundException, ImageNotSupportedException, QueryMalformedException {
        /* find */
        final Database database = databaseService.find(databaseId);
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database);
        final Session session = factory.openSession();
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final NativeQuery<?> query = session.createSQLQuery(statement.getStatement());
        final int affectedTuples;
        try {
            log.trace("execute raw view-only query {}", statement);
            affectedTuples = query.executeUpdate();
            log.info("Execution on database id {} affected {} rows", databaseId, affectedTuples);
            session.getTransaction()
                    .commit();
        } catch (SQLGrammarException e) {
            session.close();
            factory.close();
            throw new QueryMalformedException("Query not valid for this database", e);
        }
        /* map the result to the tables (with respective columns) from the statement metadata */
        final List<TableColumn> columns = parseColumns(databaseId, statement);
        final QueryResultDto result = queryMapper.resultListToQueryResultDto(columns, query.getResultList());
        session.close();
        factory.close();
        log.debug("query id {}", result.getId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public QueryResultDto findAll(Long containerId, Long databaseId, Long tableId, Instant timestamp, Long page,
                                  Long size, String sortBy, Boolean sortDesc) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException, TableMalformedException,
            PaginationException, ContainerNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        final NativeQuery<?> query = session.createSQLQuery(queryMapper.tableToRawFindAllQuery(table, timestamp, size,
                page, sortBy, sortDesc));
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.trace("Found {} tuples in database id {}", affectedTuples, databaseId);
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
    @Transactional(readOnly = true)
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
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        final NativeQuery<BigInteger> query = session.createSQLQuery(queryMapper.tableToRawCountAllQuery(table, timestamp));
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
            log.trace("counted {} tuples in table id {}", affectedTuples, tableId);
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
    @Transactional(readOnly = true)
    public Integer insert(Long containerId, Long databaseId, Long tableId, TableCsvDto data)
            throws ImageNotSupportedException, TableMalformedException, DatabaseNotFoundException,
            TableNotFoundException, ContainerNotFoundException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        if (data.getData().size() == 0) return null;
        /* replace */
        data = dataMapper.replace(data, table);
        /* insert */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.tableCsvDtoToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        log.trace("query with parameters {}", query.setParameterList(1, raw.getData()));
        return insert(query, session, factory);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer insert(Long containerId, Long databaseId, Long tableId, ImportDto data)
            throws ImageNotSupportedException, TableMalformedException, DatabaseNotFoundException,
            TableNotFoundException, ContainerNotFoundException, FileStorageException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* replace */
        commaValueService.replace(table, data.getLocation());
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = queryMapper.pathToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        log.info("Import file into table with id {}", tableId);
        log.debug("Import file {} into table {}", data, table);
        return insert(query, session, factory);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource export(Long containerId, Long databaseId, Long tableId, Instant timestamp)
            throws ImageNotSupportedException, TableMalformedException, DatabaseNotFoundException,
            TableNotFoundException, FileStorageException {
        /* find */
        final Database database = databaseService.find(databaseId);
        final Table table = tableService.find(databaseId, tableId);
        /* run query */
        final long startSession = System.currentTimeMillis();
        final SessionFactory factory = getSessionFactory(database, true);
        final Session session = factory.openSession();
        log.trace("opened hibernate session in {} ms", System.currentTimeMillis() - startSession);
        session.beginTransaction();
        /* prepare the statement */
        final ExportTableRawQuery statement = queryMapper.tableToExportRawQuery(table, timestamp);
        final NativeQuery<?> query = session.createSQLQuery(statement.getStatement());
        final int affectedTuples;
        try {
            affectedTuples = query.executeUpdate();
        } catch (PersistenceException e) {
            session.close();
            factory.close();
            log.error("Could not export data: {}", e.getMessage());
            log.throwing(e);
            throw new TableMalformedException("Could not export data", e);
        }
        session.getTransaction()
                .commit();
        session.close();
        factory.close();
        log.trace("query affected {} rows", affectedTuples);
        try {
            final MultipartFile file = new MockMultipartFile(statement.getFilename(), Files.readAllBytes(
                    Paths.get("/tmp/" + statement.getFilename())));
            return new InputStreamResource(file.getInputStream());
        } catch (IOException e) {
            log.error("Failed to mock multipart file");
            log.throwing(e);
            throw new FileStorageException("Failed to mock multipart file", e);
        }
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

        log.debug("parsed {} columns", columns.size());
        log.trace("parsed columns {}", columns);
        return columns;
    }

}
