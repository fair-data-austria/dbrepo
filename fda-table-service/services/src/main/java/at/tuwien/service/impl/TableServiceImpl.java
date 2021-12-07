package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import at.tuwien.exception.*;
import at.tuwien.mapper.AmqpMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.TableService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

@Log4j2
@Service
public class TableServiceImpl extends JdbcConnector implements TableService {

    private final DatabaseRepository databaseRepository;
    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final AmqpMapper amqpMapper;

    @Autowired
    public TableServiceImpl(TableRepository tableRepository, DatabaseRepository databaseRepository,
                            ImageMapper imageMapper, TableMapper tableMapper, AmqpMapper amqpMapper) {
        super(imageMapper, tableMapper);
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.tableMapper = tableMapper;
        this.amqpMapper = amqpMapper;
    }

    @Override
    @Transactional
    public List<Table> findAll() {
        return tableRepository.findAll();
    }

    @Override
    @Transactional
    public List<Table> findAllForDatabaseId(Long databaseId) throws DatabaseNotFoundException {
        final Optional<Database> database;
        try {
            database = databaseRepository.findById(databaseId);
        } catch (EntityNotFoundException e) {
            log.error("Unable to find database {}", databaseId);
            throw new DatabaseNotFoundException("Unable to find database.");
        }
        if (database.isEmpty()) {
            log.error("Unable to find database {}", databaseId);
            throw new DatabaseNotFoundException("Unable to find database.");
        }
        return tableRepository.findByDatabase(database.get());
    }

    @Override
    @Transactional
    public void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException {
        final Table table = findById(databaseId, tableId);
        try {
            delete(table);
        } catch (SQLException e) {
            log.error("Could not delete database: {}", e.getMessage());
            throw new DataProcessingException("could not delete table", e);
        }
        tableRepository.delete(table);
        log.info("Deleted table {}", table.getId());
        log.debug("Deleted table {}", table);
    }

    @Override
    @Transactional
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(findDatabase(databaseId), tableId);
        if (table.isEmpty()) {
            log.error("Table {} not found in database {}", tableId, databaseId);
            throw new TableNotFoundException("table not found in database");
        }
        return table.get();
    }

    @Override
    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException {
        if (createDto.getName().contains("-")) {
            log.error("Table name cannot contain -");
            throw new TableMalformedException("Table name cannot contain -");
        }
        log.trace("create table in db {} with request {}", databaseId, createDto);
        final Database database = findDatabase(databaseId);
        /* create database in container */
        try {
            create(database, createDto);
        } catch (SQLException | IOException e) {
            log.error("Could not create table via JDBC: {}", e.getMessage());
            throw new DataProcessingException("could not create table", e);
        }
        /* save in metadata db */
        final Table mappedTable = tableMapper.tableCreateDtoToTable(createDto);
        mappedTable.setDatabase(database);
        mappedTable.setTdbid(databaseId);
        mappedTable.setColumns(List.of()); // TODO: our metadata db model (primary keys x3) does not allow this currently
        mappedTable.setTopic(amqpMapper.queueName(mappedTable));
        final Table table;
        try {
            table = tableRepository.save(mappedTable);
        } catch (EntityNotFoundException e) {
            log.error("Could not create table compound key: {}", e.getMessage());
            throw new DataProcessingException("failed to create table compound key", e);
        }
        /* we cannot insert columns at the same time since they depend on the table id */
        for (int i = 0; i < createDto.getColumns().length; i++) {
            final TableColumn column = tableMapper.columnCreateDtoToTableColumn(createDto.getColumns()[i]);
            column.setOrdinalPosition(i);
            column.setCdbid(databaseId);
            column.setTid(table.getId());
            table.getColumns()
                    .add(column);
        }
        /* if no primary key is yet assigned, we generate an invisible auto-generated sequence */
        if (table.getColumns().stream().noneMatch(TableColumn::getIsPrimaryKey)) {
            table.getColumns()
                    .add(TableColumn.builder()
                            .name("ID")
                            .internalName("id")
                            .columnType(TableColumnType.NUMBER)
                            .autoGenerated(true)
                            .isPrimaryKey(true)
                            .isUnique(true)
                            .isNullAllowed(false)
                            .ordinalPosition(table.getColumns().size())
                            .build());
        }
        /* update table in metadata db */
        final Table out;
        try {
            out = tableRepository.save(table);
        } catch (EntityNotFoundException e) {
            log.error("Could not create column compound key: {}", e.getMessage());
            throw new DataProcessingException("failed to create column compound key", e);
        }
        log.info("Created table {}", out.getId());
        log.debug("created table: {}", out);
        return out;
    }

    @Override
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("Could not find database with id {} in metadata database", id);
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    @Override
    public TableCsvDto readCsv(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException,
            ArrayIndexOutOfBoundsException {
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(data.getDelimiter())
                .build();
        final Reader fileReader = new InputStreamReader(file.getInputStream());
        final List<List<String>> cells = new LinkedList<>();
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .withSkipLines(data.getSkipHeader() ? 1 : 0)
                .build();
        final List<Map<String, Object>> records = new LinkedList<>();
        reader.readAll()
                .forEach(x -> cells.add(Arrays.asList(x)));
        /* map to the map-list structure */
        for (List<String> row : cells) {
            final Map<String, Object> record = new HashMap<>();
            for (int i = 0; i < table.getColumns().size(); i++) {
                record.put(table.getColumns().get(i).getInternalName(), row.get(i));
            }
            /* when the nullElement itself is null, nothing to do */
            if (data.getNullElement() != null) {
                record.replaceAll((key, value) -> value.equals(data.getNullElement()) ? null : value);
            }
            log.trace("processed {}", row);
            records.add(record);
        }
        return TableCsvDto.builder()
                .data(records)
                .build();
    }


}
