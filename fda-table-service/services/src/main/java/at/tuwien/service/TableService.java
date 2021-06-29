package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvInformationDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class TableService {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                        PostgresService postgresService, TableMapper tableMapper) {
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
        this.tableMapper = tableMapper;
    }

    @Transactional
    public List<Table> findAll(Long databaseId) throws DatabaseNotFoundException, TableNotFoundException {
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
        return database.get().getTables();
    }

    @Transactional
    public void delete(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException, TableMalformedException, DataProcessingException, DatabaseNotFoundException, ImageNotSupportedException {
        final Table table = findById(databaseId, tableId);
        postgresService.deleteTable(table);
        tableRepository.deleteById(tableId);
    }

    @Transactional
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException {
        final Optional<Table> table = tableRepository.findByDatabaseAndId(findDatabase(databaseId), tableId);
        if (table.isEmpty()) {
            log.error("table {} not found in database {}", tableId, databaseId);
            throw new TableNotFoundException("table not found in database");
        }
        return table.get();
    }

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
        log.debug("found database: {}", database.get());
        return database.get();
    }

    @Transactional
    public Table create(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException, DataProcessingException {
        final Database database = findDatabase(databaseId);
        /* create database in container */
        postgresService.createTable(database, createDto);
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

    @Transactional
    public QueryResultDto insert(Long databaseId, Long tableId, MultipartFile file) throws TableNotFoundException,
            ImageNotSupportedException, DatabaseNotFoundException, DatabaseConnectionException, DataProcessingException {
        Table t = findById(databaseId, tableId);
        Database d = findDatabase(databaseId);
        log.debug(t.toString());
        log.info("Reading CSV file {}", file.getName());
        List<Map<String, Object>> processedData = readCsv(file, t);
        List<String> headers = new ArrayList<>();
        for (Map<String, Object> m : processedData) {
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                headers.add(entry.getKey());
            }
            break;
        }
        return postgresService.insertIntoTable(d, t, processedData, headers);
    }

    /* helper functions */

    private List<Map<String, Object>> readCsv(MultipartFile file, Table table) throws FileStorageException {
        ICsvMapReader mapReader = null;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);

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

    private String[] readHeader(MultipartFile file) throws IOException {
        ICsvMapReader mapReader = null;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);

            String[] header = mapReader.getHeader(true);
            return header;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }
        return null;
    }

    // TODO ms what is this for? It does ony print to stdout
    public QueryResultDto showData(Long databaseId, Long tableId) throws ImageNotSupportedException,
            DatabaseNotFoundException, TableNotFoundException, DatabaseConnectionException, DataProcessingException {
        final Table tmpTable = findById(databaseId, tableId);
        log.debug("=================> {}", tmpTable);
        QueryResultDto queryResult = postgresService.getAllRows(findDatabase(databaseId), tmpTable);
        for (Map<String, Object> m : queryResult.getResult()) {
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                log.debug("{}: {}", entry.getKey(), entry.getValue());
            }
        }
        return queryResult;
    }

    public Table create(Long databaseId, MultipartFile file, TableCsvInformationDto tableCSVInformation) {
        try {
            String[] header = readHeader(file);
            log.debug("table csv info: {}", tableCSVInformation);
            TableCreateDto tcd = new TableCreateDto();
            tcd.setName(tableCSVInformation.getName());
            tcd.setDescription(tableCSVInformation.getDescription());
            ColumnCreateDto[] cdtos = new ColumnCreateDto[header.length];
            log.debug("header: {}", header);
            for (int i = 0; i < header.length; i++) {
                ColumnCreateDto c = new ColumnCreateDto();
                c.setName(header[i]);
                c.setType(tableCSVInformation.getColumns().get(i));
                c.setNullAllowed(true);
                //TODO FIX THAT not only id is primary key
                if (header[i].equals("id")) {
                    c.setPrimaryKey(true);
                } else {
                    c.setPrimaryKey(false);
                }
                cdtos[i] = c;
            }
            tcd.setColumns(cdtos);
            Table table = create(databaseId, tcd);
            QueryResultDto insert = insert(databaseId, table.getId(), file);
            return table;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    public Table create(Long databaseId, TableCsvInformationDto tableCSVInformation) throws IOException {
        Path path = Paths.get("/tmp/" + tableCSVInformation.getFileLocation());
        String contentType = "multipart/form-data";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile multipartFile = new MockMultipartFile(tableCSVInformation.getFileLocation(),
                tableCSVInformation.getFileLocation(), contentType, content);
        Files.deleteIfExists(path);
        return create(databaseId, multipartFile, tableCSVInformation);
    }


}
