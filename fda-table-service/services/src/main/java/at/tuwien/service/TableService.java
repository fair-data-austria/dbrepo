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
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
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
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Log4j2
@Service
public class TableService extends JdbcConnector {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                        ImageMapper imageMapper, TableMapper tableMapper) {
        super(imageMapper, tableMapper);
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.tableMapper = tableMapper;
    }

    @Transactional
    public List<Table> findAll(Long databaseId) throws DatabaseNotFoundException {
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

    @Transactional
    public void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException {
        final Table table = findById(databaseId, tableId);
        try {
            delete(table);
        } catch (SQLException e) {
            throw new DataProcessingException("could not delete table", e);
        }
        tableRepository.delete(table);
        log.info("Deleted table {}", table.getId());
        log.debug("Deleted table {}", table);
    }

    @Transactional
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(findDatabase(databaseId), tableId);
        if (table.isEmpty()) {
            log.error("table {} not found in database {}", tableId, databaseId);
            throw new TableNotFoundException("table not found in database");
        }
        return table.get();
    }

    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, EntityNotSupportedException,
            ArbitraryPrimaryKeysException {
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
     * @throws DataProcessingException    The xml could not be mapped.
     */
    @Transactional
    public void insertFromFile(Long databaseId, Long tableId, TableInsertDto data)
            throws TableNotFoundException, ImageNotSupportedException, DatabaseNotFoundException, FileStorageException,
            TableMalformedException {
        final Table table = findById(databaseId, tableId);
        final TableCsvDto values;
        try {
            values = readCsv(data, table);
        } catch (IOException | CsvException e) {
            log.error("failed to parse csv {}", e.getMessage());
            throw new FileStorageException("failed to parse csv", e);
        }
        /* hibernate inserts one line after another (batch insert not supported), so we can do the same now and take out some complexity of the code */
        try {
            insert(table, values);
        } catch (SQLException e) {
            log.error("could not insert data {}", e.getMessage());
            throw new TableMalformedException("could not insert data", e);
        }
        log.info("Inserted {} csv records into table id {}", values.getData().size(), tableId);
    }

    /* helper functions */

    private Database findDatabase(Long id) throws DatabaseNotFoundException, ImageNotSupportedException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        if (!database.get().getContainer().getImage().getRepository().equals("postgres")) {
            log.error("Right now only PostgreSQL is supported!");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return database.get();
    }

    private TableCsvDto readCsv(TableInsertDto data, Table table) throws IOException, CsvException {
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(data.getDelimiter())
                .build();
        final Reader fileReader = new InputStreamReader(data.getCsv().getInputStream());
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

    @Deprecated
    private List<Map<String, Object>> readCsv(MultipartFile file, Table table) throws FileStorageException {
        ICsvMapReader mapReader = null;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            mapReader = new CsvMapReader(reader, STANDARD_PREFERENCE);

            String[] header = mapReader.getHeader(true);
            for (String s : header) {
                System.out.println(s);
            }
            String[] columnHeader = new String[header.length];
            final CellProcessor[] processors = new CellProcessor[header.length];
            for (int i = 0; i < header.length; i++) {
                int finalI = i;
                Optional<TableColumn> tcx = table.getColumns().stream().filter(x -> x.getName().equals(header[finalI])).findFirst();
                TableColumn tc;
                if (tcx.isPresent()) {
                    tc = tcx.get();
                    columnHeader[i] = tc.getName();
                    processors[i] = tc.getIsNullAllowed() ? new org.supercsv.cellprocessor.Optional() : new NotNull();
                }
            }
            List<Map<String, Object>> listMaps = new ArrayList<>();
            Map<String, Object> tableMap;
            while ((tableMap = mapReader.read(columnHeader, processors)) != null) {
                listMaps.add(tableMap);
            }

            return listMaps;

        } catch (IOException e) {
            throw new FileStorageException("csv was unprocessible", e);
        } finally {
            if (mapReader != null) {
                try {
                    mapReader.close();
                } catch (IOException e) {
                    // cannot catch anymore
                }
            }
        }
    }

    @Deprecated
    private String[] readHeader(MultipartFile file) throws IOException {
        ICsvMapReader mapReader = null;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            mapReader = new CsvMapReader(reader, STANDARD_PREFERENCE);

            String[] header = mapReader.getHeader(true);
            return header;

        } catch (IOException e) {
            // FIXME throw it, improves stability!
            e.printStackTrace();
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }
        return null;
    }


    // TODO ms what is this for? It does ony print to stdout
    @Deprecated
    public QueryResultDto showData(Long databaseId, Long tableId) throws ImageNotSupportedException,
            DatabaseNotFoundException, TableNotFoundException, DatabaseConnectionException, DataProcessingException {
//        QueryResultDto queryResult = postgresService.getAllRows(findDatabase(databaseId), findById(databaseId, tableId));
//        for (Map<String, Object> m : queryResult.getResult()) {
//            for (Map.Entry<String, Object> entry : m.entrySet()) {
//                log.debug("{}: {}", entry.getKey(), entry.getValue());
//            }
//        }
//        return queryResult;
        return null;
    }

    private CellProcessor[] getProcessors(Integer size, TableInsertDto data) {
        final CellProcessor[] processors = new CellProcessor[size];
        for (int i = 0; i < processors.length; i++) {
            if (data.getNullElement() == null) {
                processors[i] = new NotNull();
            } else {
                processors[i] = new Equals(data.getNullElement());
            }
        }
        return processors;
    }


}
