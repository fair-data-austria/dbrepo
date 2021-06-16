package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
    private final ImageMapper imageMapper;
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                        ImageMapper imageMapper, TableMapper tableMapper) {
        super(imageMapper);
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.imageMapper = imageMapper;
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
    public void delete(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, SQLException {
        final Table table = findById(databaseId, tableId);
        final Database database = findDatabase(databaseId);
        final DSLContext context = open(database);
        context.dropTable(table.getName());
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

    @Transactional
    public Table create(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseNotFoundException, DataProcessingException, EntityNotSupportedException, SQLException,
            ArbitraryPrimaryKeysException {
        final Database database = findDatabase(databaseId);
        if (tableRepository.findByDatabaseAndName(database, createDto.getName()).isPresent()) {
            // DEVNOTE note that hibernate actually has no problem with updating the table, but we do not want this behavior for this method
            log.warn("table with name '{}' already exists in database {}", createDto.getName(), databaseId);
            throw new EntityNotSupportedException("table names must be unique, there exists already a table");
        }
        final DSLContext context = open(database);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, createDto)
                .execute();
        /* save in metadata db */
        Table mappedTable = tableMapper.tableCreateDtoToTable(createDto);
        mappedTable.setDatabase(database);
        mappedTable.setTdbid(databaseId);
        mappedTable.setColumns(List.of());
        try {
            mappedTable = tableRepository.save(mappedTable);
        } catch (EntityNotFoundException e) {
            log.error("failed to create table compound key: {}", e.getMessage());
            throw new DataProcessingException("failed to create table compound key", e);
        }
        /* we cannot insert columns at the same time since they depend on the table id */
        for (int i = 0; i < createDto.getColumns().length; i++) {
            final TableColumn column = tableMapper.columnCreateDtoToTableColumn(createDto.getColumns()[i]);
            column.setOrdinalPosition(i);
            column.setCdbid(databaseId);
            column.setTid(mappedTable.getId());
            mappedTable.getColumns()
                    .add(column);
        }
        /* update table in metadata db */
        final Table out;
        try {
            out = tableRepository.save(mappedTable);
        } catch (EntityNotFoundException e) {
            log.error("failed to create column compound key: {}", e.getMessage());
            throw new DataProcessingException("failed to create column compound key", e);
        }
        log.info("Created table {}", out.getId());
        log.debug("created table: {}", out);
        return out;
    }

    /**
     * TODO this needs to be at some different endpoint
     * Insert data from a file into a table of a database with possible null values (denoted by a null element).
     *
     * @param databaseId The database.
     * @param tableId    The table.
     * @param file       The file.
     * @param insertDto  The null element.
     * @throws TableNotFoundException     The table does not exist in the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DatabaseNotFoundException  The database does not exist in the metdata database.
     * @throws FileStorageException       The CSV could not be parsed.
     * @throws DataProcessingException    The xml could not be mapped.
     */
    @Transactional
    public void insertFromFile(Long databaseId, Long tableId, TableInsertDto insertDto, MultipartFile file)
            throws TableNotFoundException, ImageNotSupportedException, DatabaseNotFoundException, FileStorageException {
        final Table table = findById(databaseId, tableId);
        final Map<String, List<String>> cells;
        try {
            cells = readCsv(file, insertDto);
        } catch (IOException e) {
            throw new FileStorageException("failed to parse csv", e);
        }
        /* hibernate inserts one line after another (batch insert not supported), so we can do the same now and take out some complexity of the code */
//        try {
//            insertFromCollection(table, cells);
//        } catch (ConstructorNotFoundException | ReflectAccessException e) {
//            throw new TableMalformedException("could not insert via reflect", e);
//        }
        log.info("Inserted .csv to table {}", tableId);
        log.debug("Inserted .csv ({} rows) into table {}", cells.size(), tableId);
    }

    /* helper functions */

    private Map<String, List<String>> readCsv(MultipartFile file, TableInsertDto insertDto) throws IOException {
        final Map<String, List<String>> records = new HashMap<>();
        final Reader fileReader = new InputStreamReader(file.getInputStream());
        final CSVReader csvReader = new CSVReader(fileReader);
        final List<String> headers = Arrays.asList(new CsvMapReader(fileReader, STANDARD_PREFERENCE).getHeader(true));
        final List<List<String>> cells = new LinkedList<>();
        /* read each row into a list */
        String[] values = null;
        while ((values = csvReader.readNext()) != null) {
            cells.add(Arrays.asList(values));
        }
        /* init the map-list structure */
        for (String key : headers) {
            records.put(key, new LinkedList<>());
        }
        /* map to the map-list structure */
        for (List<String> row : cells) {
            for (int i = 0; i < headers.size(); i++) {
                if (insertDto.getNullElement() != null) {
                    row.replaceAll(input -> input.equals(insertDto.getNullElement()) ? null : input);
                }
                records.get(headers.get(i)).add(row.get(i));
            }
        }
        return records;
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


}
