package at.tuwien.service.impl;

import at.tuwien.CreateTableRawQuery;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TableServiceImpl extends HibernateConnector implements TableService {

    private final TableMapper tableMapper;
    private final TableRepository tableRepository;
    private final DatabaseService databaseService;

    @Autowired
    public TableServiceImpl(TableMapper tableMapper, TableRepository tableRepository,
                            DatabaseService databaseService) {
        this.tableMapper = tableMapper;
        this.tableRepository = tableRepository;
        this.databaseService = databaseService;
    }

    @Override
    public List<Table> findAll(Long databaseId) throws DatabaseNotFoundException {
        final Database database = databaseService.findDatabase(databaseId);
        return tableRepository.findByDatabase(database);
    }

    @Override
    @Transactional
    public void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {
        /* find */
        final Database database = databaseService.findDatabase(databaseId);
        final Table table = findById(databaseId, tableId);
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        session.createSQLQuery(tableMapper.tableToDropTableRawQuery(table));
        transaction.commit();
        session.close();
    }

    @Override
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        final Database database = databaseService.findDatabase(databaseId);
        final Optional<Table> optional = tableRepository.findByDatabaseAndId(database, tableId);
        if (optional.isEmpty()) {
            log.error("Failed to find table with id {} in metadata database", tableId);
            throw new TableNotFoundException("Table not found");
        }
        return optional.get();
    }

    @Override
    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, TableMalformedException {
        /* find */
        final Database database = databaseService.findDatabase(databaseId);
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        final CreateTableRawQuery query = tableMapper.tableToCreateTableRawQuery(database, createDto);
        log.debug("Create Table Raw query is [{}]", query);

        if (query.getGenerated()) {
            /* in case the id column needs to be generated, we need to generate the sequence too */
            session.createSQLQuery(tableMapper.tableToCreateSequenceRawQuery(database, createDto))
                    .executeUpdate();
        }
        session.createSQLQuery(query.getQuery())
                .executeUpdate();
        transaction.commit();
        session.close();
        int[] idx = {0};
        /* save in metadata database */
        final Table table = tableRepository.save(tableMapper.tableCreateDtoToTable(database, createDto));
        table.setColumns(Arrays.stream(createDto.getColumns())
                .map(tableMapper::columnCreateDtoToTableColumn)
                .map(column -> tableMapper.tableColumnToTableColumn(table, column, query))
                .collect(Collectors.toList()));
        /* set the ordinal position for the columns */
        table.getColumns()
                .forEach(column -> {
                    column.setOrdinalPosition(idx[0]++);
                });
        log.debug("Saving table {}",table);
        return tableRepository.save(table);
    }
}
