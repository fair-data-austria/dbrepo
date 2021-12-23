package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableColumnRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.TableService;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TableServiceImpl extends HibernateConnector implements TableService {

    private final TableMapper tableMapper;
    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final TableColumnRepository tableColumnRepository;

    @Autowired
    public TableServiceImpl(TableMapper tableMapper, TableRepository tableRepository,
                            DatabaseRepository databaseRepository, TableColumnRepository tableColumnRepository) {
        this.tableMapper = tableMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.tableColumnRepository = tableColumnRepository;
    }

    @Override
    public List<Table> findAll(Long databaseId) throws DatabaseNotFoundException {
        final Database database = findDatabase(databaseId);
        return tableRepository.findByDatabase(database);
    }

    @Override
    @Transactional
    public void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException {
        final Database database = findDatabase(databaseId);
        final Table table = findById(databaseId, tableId);
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        session.createSQLQuery(tableMapper.tableToDropTableRawQuery(table));
        transaction.commit();
        session.close();
    }

    @Override
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        final Database database = findDatabase(databaseId);
        final Optional<Table> optional = tableRepository.findByDatabaseAndId(database, tableId);
        if (optional.isEmpty()) {
            log.error("Failed to find table with id {} in metadata database", tableId);
            throw new TableNotFoundException("Table not found");
        }
        return optional.get();
    }

    @Override
    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException, DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException {
        final Database database = findDatabase(databaseId);
        final Session session = getSessionFactory(database)
                .openSession();
        final Transaction transaction = session.beginTransaction();
        session.createSQLQuery(tableMapper.tableToCreateTableRawQuery(database, createDto))
                .executeUpdate();
        transaction.commit();
        session.close();
        int[] idx = {0};
        /* save in metadata database */
        final Table prototype = tableMapper.tableCreateDtoToTable(database, createDto);
        prototype.setColumns(List.of());
        final Table table = tableRepository.save(prototype);
        table.setColumns(Arrays.stream(createDto.getColumns())
                .map(tableMapper::columnCreateDtoToTableColumn)
                .map(c -> tableMapper.tableColumnToTableColumn(table, c))
                .collect(Collectors.toList()));
        table.getColumns()
                .forEach(c -> c.setOrdinalPosition(idx[0]++));
        return tableRepository.save(table);
    }

    @Override
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> optional = databaseRepository.findById(id);
        if (optional.isEmpty()) {
            log.error("Failed to find database with id {} in metadata database", id);
            throw new DatabaseNotFoundException("Database not found");
        }
        return optional.get();
    }

    @Override
    public TableCsvDto readCsv(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException, ArrayIndexOutOfBoundsException {
        return null;
    }
}
