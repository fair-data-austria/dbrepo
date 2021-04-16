package at.tuwien.service;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.entity.ColumnType;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.entity.TableColumn;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final List<Table> tables;
        try {
            tables = tableRepository.findByDatabase(database.get());
        } catch (EntityNotFoundException e) {
            log.error("Unable to find tables for database {}.", database);
            throw new DatabaseNotFoundException("Unable to find tables.");
        }
        return tables;
    }

    public Table findById(Long databaseId, Long tableId) {
        final Database tmp = new Database();
        tmp.setId(databaseId);
        return tableRepository.findByDatabaseAndId(tmp, tableId);
    }

    private Database findDatabase(Long id) throws DatabaseNotFoundException, ImageNotSupportedException {
        final Optional<Database> database;
        try {
            database = databaseRepository.findById(id);
        } catch (EntityNotFoundException e) {
            log.error("database not found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database", e);
        }
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        log.debug("retrieved db {}", database);
        if (!database.get().getContainer().getImage().getRepository().equals("postgres")) {
            log.error("Right now only PostgreSQL is supported!");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return database.get();
    }

    @Transactional
    public Table create(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException {
        final Database database = findDatabase(databaseId);

        /* save in metadata db */
        postgresService.createTable(database, createDto);
        final Table table = tableMapper.tableCreateDtoToTable(createDto);
        table.setDatabase(database);
        table.setInternalName(tableMapper.columnNameToString(table.getName()));
        final Table out = tableRepository.save(table);
        log.debug("saved table {}", out);
        log.info("Created table {} in database {}", out.getId(), out.getDatabase().getId());
        return out;
    }

    public Table insert(Long databaseId, Long tableId, MultipartFile file) throws Exception {
        Table t = findById(databaseId, tableId);
        Database d = findDatabase(databaseId);
        log.debug(t.toString());
        log.info("Reading CSV file {}", file.getName());
        List<Map<String, Object>> processedData = readCsv(file, t);
        List<String> headers = new ArrayList<>();
        for (Map<String, Object> m : processedData ) {
            for ( Map.Entry<String,Object> entry : m.entrySet()) {
                headers.add(entry.getKey());
            }
            break;
        }
        postgresService.insertIntoTable(d, t,processedData, headers);
        return null;
    }

    private List<Map<String, Object>> readCsv(MultipartFile file, Table table) throws IOException {
        ICsvMapReader mapReader = null;
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);

            String[] header = mapReader.getHeader(true);
            for(String s : header) {
                System.out.println(s);
            }
            String[] columnHeader = new String[header.length];
            final CellProcessor[] processors = new CellProcessor[header.length];
            for(int i = 0; i < header.length; i++) {
                int finalI = i;
                TableColumn tc = table.getColumns().stream().filter(x -> x.getName().equals(header[finalI])).findFirst().get();
                columnHeader[i] = tc.getName();
                processors[i] = tc.getIsNullAllowed() ? new org.supercsv.cellprocessor.Optional(): new NotNull();
            }

            List<Map<String, Object>> listMaps = new ArrayList<>();
            Map<String, Object> tableMap;
            while( (tableMap = mapReader.read(columnHeader, processors)) != null ) {
                listMaps.add(tableMap);
            }

            return listMaps;

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if( mapReader != null ) {
                mapReader.close();
            }
        }
        return null;
    }

}
