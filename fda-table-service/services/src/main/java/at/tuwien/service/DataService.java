package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Log4j2
@Service
public class DataService extends JdbcConnector {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final TableMapper tableMapper;

    @Autowired
    public DataService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                       ImageMapper imageMapper, TableMapper tableMapper, QueryMapper queryMapper) {
        super(imageMapper, tableMapper, queryMapper);
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.tableMapper = tableMapper;
    }

    @Transactional
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(findDatabase(databaseId), tableId);
        if (table.isEmpty()) {
            log.error("table {} not found in database {}", tableId, databaseId);
            throw new TableNotFoundException("table not found in database");
        }
        return table.get();
    }

    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException {
        final Database database = findDatabase(databaseId);
        /* create database in container */
        try {
            create(database, createDto);
        } catch (SQLException e) {
            throw new DataProcessingException("could not create table", e);
        }
        /* save in metadata db */
        final Table mappedTable = tableMapper.tableCreateDtoToTable(createDto);
        mappedTable.setDatabase(database);
        mappedTable.setTdbid(databaseId);
        mappedTable.setColumns(List.of()); // TODO: our metadata db model (primary keys x3) does not allow this currently
        final Table table;
        try {
            table = tableRepository.save(mappedTable);
        } catch (EntityNotFoundException e) {
            log.error("failed to create table compound key: {}", e.getMessage());
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
        /* update table in metadata db */
        try {
            tableRepository.save(table);
        } catch (EntityNotFoundException e) {
            log.error("failed to create column compound key: {}", e.getMessage());
            throw new DataProcessingException("failed to create column compound key", e);
        }
        log.info("Created table {}", table.getId());
        log.debug("created table: {}", table);
        return table;
    }

    /**
     * TODO this needs to be at some different endpoint
     * Insert data from a file into a table of a database with possible null values (denoted by a null element).
     *
     * @param databaseId The database.
     * @param tableId    The table.
     * @param data       The null element and delimiter.
     * @throws TableNotFoundException     The table does not exist in the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DatabaseNotFoundException  The database does not exist in the metdata database.
     * @throws FileStorageException       The CSV could not be parsed.
     */
    @Transactional
    public void insertFromFile(Long databaseId, Long tableId, TableInsertDto data)
            throws TableNotFoundException, ImageNotSupportedException, DatabaseNotFoundException, FileStorageException,
            TableMalformedException {
        final Table table = findById(databaseId, tableId);
        final TableCsvDto values;
        try {
            values = readCsv(table, data);
            log.debug("read {} rows from csv", values.getData().size());
        } catch (IOException | CsvException | ArrayIndexOutOfBoundsException e) {
            log.error("failed to parse csv {}", e.getMessage());
            throw new FileStorageException("failed to parse csv", e);
        }
        try {
            insert(table, values);
        } catch (SQLException | DataAccessException e) {
            log.error("could not insert data {}", e.getMessage());
            throw new TableMalformedException("could not insert data", e);
        }
        log.info("Inserted {} csv records into table id {}", values.getData().size(), tableId);
    }

    /* helper functions */

    @Transactional
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    public TableCsvDto readCsv(Table table, TableInsertDto data) throws IOException, CsvException,
            ArrayIndexOutOfBoundsException {
        log.debug("insert into table {} with params {}", table, data);
        if (data.getDelimiter() == null) {
            data.setDelimiter(',');
        }
        if (!data.getCsvLocation().startsWith("test:")) {
            data.setCsvLocation("/tmp/" + data.getCsvLocation());
        } else {
            data.setCsvLocation(data.getCsvLocation().substring(5));
        }
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(data.getDelimiter())
                .build();
        final MultipartFile multipartFile = new MockMultipartFile(data.getCsvLocation(), Files.readAllBytes(Paths.get(data.getCsvLocation())));
        final Reader fileReader = new InputStreamReader(multipartFile.getInputStream());
        final List<List<String>> cells = new LinkedList<>();
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        final List<Map<String, Object>> records = new LinkedList<>();
        reader.readAll()
                .forEach(x -> cells.add(Arrays.asList(x)));
        log.debug("csv raw row size {}, cells raw size {}", reader.readAll().size(), cells.size());
        /* map to the map-list structure */
        for (int i = (data.getSkipHeader() ? 1 : 0); i < cells.size(); i++) {
            final Map<String, Object> record = new HashMap<>();
            final List<String> row = cells.get(i);
            log.trace("row {}: {}", i, row);
            for (int j = 0; j < table.getColumns().size(); j++) {
                record.put(table.getColumns().get(j).getInternalName(), row.get(j));
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

    @Transactional
    public QueryResultDto selectAll(Long databaseId, Long tableId) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException {
        final QueryResultDto queryResult;
        try {
            queryResult = selectAll(findById(databaseId, tableId));
        } catch (SQLException e) {
            log.error("Could not find data: {}", e.getMessage());
            throw new DatabaseConnectionException(e);
        }
        log.trace("found data {}", queryResult);
        return queryResult;
    }


}
