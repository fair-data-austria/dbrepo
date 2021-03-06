package at.tuwien.service.impl;

import at.tuwien.CreateTableRawQuery;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.ContainerService;
import at.tuwien.service.DatabaseService;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TableServiceImpl extends HibernateConnector implements TableService {

    private final TableMapper tableMapper;
    private final TableRepository tableRepository;
    private final DatabaseService databaseService;
    private final ContainerService containerService;

    @Autowired
    public TableServiceImpl(TableMapper tableMapper, TableRepository tableRepository,
                            DatabaseService databaseService, ContainerService containerService) {
        this.tableMapper = tableMapper;
        this.tableRepository = tableRepository;
        this.databaseService = databaseService;
        this.containerService = containerService;
    }

    @Override
    public List<Table> findAll(Long containerId, Long databaseId) throws DatabaseNotFoundException {
        final Database database = databaseService.findDatabase(databaseId);
        return tableRepository.findByDatabase(database);
    }

    @Override
    @Transactional
    public void deleteTable(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Database database = databaseService.findDatabase(databaseId);
        final Table table = findById(containerId, databaseId, tableId);
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        session.createSQLQuery(tableMapper.tableToDropTableRawQuery(table));
        transaction.commit();
        session.close();
        log.info("Deleted table with id {}", table.getId());
        log.debug("deleted table {}", table);
    }

    @Override
    public Table findById(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException,
            DatabaseNotFoundException, ContainerNotFoundException {
        final Container container = containerService.find(containerId);
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
    public Table createTable(Long containerId, Long databaseId, TableCreateDto createDto)
            throws ImageNotSupportedException, DatabaseNotFoundException, TableMalformedException,
            TableNameExistsException, ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Database database = databaseService.findDatabase(databaseId);
        final Optional<Table> optional = tableRepository.findByInternalName(tableMapper.nameToInternalName(createDto.getName()));
        if (optional.isPresent()) {
            log.error("Table name exists in database with id {} as table id {}", database.getId(), optional.get().getId());
            throw new TableNameExistsException("Table name exists");
        }
        /* run query */
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        final CreateTableRawQuery query = tableMapper.tableToCreateTableRawQuery(database, createDto);
        log.trace("create table raw query is [{}]", query);
        if (query.getGenerated()) {
            /* in case the id column needs to be generated, we need to generate the sequence too */
            try {
                session.createSQLQuery(tableMapper.tableToCreateSequenceRawQuery(database, createDto))
                        .executeUpdate();
            } catch (PersistenceException e) {
                log.error("Table sequence exists, but table does not. Create an issue for this.");
                throw new TableNameExistsException("Sequence exists", e);
            }
            log.debug("created id sequence");
        }
        session.createSQLQuery(query.getQuery())
                .executeUpdate();
        transaction.commit();
        session.close();
        int[] idx = {0};
        /* map table */
        final Table tmp = tableMapper.tableCreateDtoToTable(createDto);
        tmp.setInternalName(tableMapper.nameToInternalName(tmp.getName()));
        tmp.setTdbid(databaseId);
        tmp.setDatabase(database);
        tmp.setTopic(tmp.getInternalName());
        tmp.setColumns(List.of());
        log.debug("mapped new table {}", tmp);
        /* save in metadata database */
        final Table table = tableRepository.save(tmp);
        table.setColumns(Arrays.stream(createDto.getColumns())
                .map(tableMapper::columnCreateDtoToTableColumn)
                .map(column -> tableMapper.tableColumnToTableColumn(table, column, query))
                .collect(Collectors.toList()));
        /* set the ordinal position for the columns */
        table.getColumns()
                .forEach(column -> {
                    column.setOrdinalPosition(idx[0]++);
                });
        log.info("Created table with id {} {}", table.getId(), query.getGenerated() ? "and auto-generated id column" : "");
        log.debug("created table {}", table);
        return tableRepository.save(table);
    }
}
