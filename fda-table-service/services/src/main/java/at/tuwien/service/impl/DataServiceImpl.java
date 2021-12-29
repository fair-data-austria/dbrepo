package at.tuwien.service.impl;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.DataMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.DataService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Optional;

@Log4j2
@Service
public class DataServiceImpl extends HibernateConnector implements DataService {

    private final DataMapper dataMapper;
    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public DataServiceImpl(DataMapper dataMapper, TableRepository tableRepository,
                           DatabaseRepository databaseRepository) {
        this.dataMapper = dataMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
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
        final Database database = findDatabase(databaseId);
        final Table table = findById(databaseId, tableId);
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        final NativeQuery<?> query = session.createSQLQuery(dataMapper.tableToRawFindAllQuery(table, timestamp, size,
                page));
        query.executeUpdate();
        transaction.commit();
        final QueryResultDto result;
        try {
            result = dataMapper.queryTableToQueryResultDto(query, table);
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
        final Database database = findDatabase(databaseId);
        final Table table = findById(databaseId, tableId);
        /* run query */
        if (data.getData().size() == 0 || data.getData().get(0).size() == 0) return;
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        /* prepare the statement */
        final InsertTableRawQuery raw = dataMapper.tableTableCsvDtoToRawInsertQuery(table, data);
        final NativeQuery<?> query = session.createSQLQuery(raw.getQuery());
        final int[] idx = {1} /* this needs to be >0 */;
        raw.getValues()
                .forEach(row -> query.setParameterList(idx[0]++, row));
        try {
            log.info("Inserted {} tuples", query.executeUpdate());
        } catch (PersistenceException e) {
            log.error("Could not insert data");
            throw new TableMalformedException("Could not insert data", e);
        }
        transaction.commit();
    }

    /**
     * Finds a table by database-table id pair
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @return The table if found in the metadata database.
     * @throws TableNotFoundException    When the table is not found in the metadata database.
     * @throws DatabaseNotFoundException When the database is not found in the metadata database.
     */
    @Transactional
    protected Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(findDatabase(databaseId), tableId);
        if (table.isEmpty()) {
            log.error("Table {} not found in database {}", tableId, databaseId);
            throw new TableNotFoundException("table not found in database");
        }
        return table.get();
    }

    /**
     * Finds a database by id.
     *
     * @param id The id.
     * @return The database when found in the metadata database.
     * @throws DatabaseNotFoundException When the database is not found in the metadata database.
     */
    @Transactional
    protected Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("No database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }


}
