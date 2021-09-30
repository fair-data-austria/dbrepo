package at.tuwien.service.impl;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.DataService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Log4j2
@Service
public class MariaDataService extends JdbcConnector implements DataService {

    private final DatabaseRepository databaseRepository;
    private final TableRepository tableRepository;
    private final QueryMapper queryMapper;

    @Autowired
    public MariaDataService(DatabaseRepository databaseRepository, TableRepository tableRepository,
                            ImageMapper imageMapper, TableMapper tableMapper, QueryMapper queryMapper) {
        super(imageMapper, tableMapper, queryMapper);
        this.queryMapper = queryMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
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

    @Override
    @Transactional
    public void insertCsv(Long databaseId, Long tableId, TableInsertDto data)
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
            insertCsv(table, values);
        } catch (SQLException | DataAccessException e) {
            log.error("could not insert data {}", e.getMessage());
            throw new TableMalformedException("could not insert data", e);
        }
        log.info("Inserted {} csv records into table id {}", values.getData().size(), tableId);
    }

    @Transactional
    protected Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    protected TableCsvDto readCsv(Table table, TableInsertDto data) throws IOException, CsvException,
            ArrayIndexOutOfBoundsException {
        log.debug("insert into table {} with params {}", table, data);
        if (data.getDelimiter() == null) {
            data.setDelimiter(',');
        }
        if (!data.getCsvLocation().startsWith("test:")) { // todo: improve this?
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
        List<String> headers = null;
        reader.readAll()
                .forEach(x -> cells.add(Arrays.asList(x)));
        log.trace("csv raw row size {}, cells raw size {}", reader.readAll().size(), cells.size());
        /* get header */
        if (data.getSkipHeader()) {
            headers = cells.get(0);
            log.debug("got headers {}", headers);
        }
        /* map to the map-list structure */
        for (int i = (data.getSkipHeader() ? 1 : 0); i < cells.size(); i++) {
            final Map<String, Object> record = new HashMap<>();
            final List<String> row = cells.get(i);
            for (int j = 0; j < table.getColumns().size(); j++) {
                /* detect if order is correct, we depend on the CsvParser library */
                if (headers != null && table.getColumns().get(j).getInternalName().equals(headers.get(j))) {
                    log.error("header out of sync, actual: {} but expected: {}", headers.get(j), table.getColumns().get(j).getInternalName());
                }
                record.put(table.getColumns().get(j).getInternalName(), row.get(j));
            }
            /* when the nullElement itself is null, nothing to do */
            if (data.getNullElement() != null) {
                record.replaceAll((key, value) -> value.equals(data.getNullElement()) ? null : value);
            }
            records.add(record);
        }
        if (headers == null || headers.size() == 0) {
            log.warn("No header check possible, possibly csv without header line or skipHeader=false provided");
        }
        log.debug("first row is {}", records.size() > 0 ? records.get(0) : null);
        return TableCsvDto.builder()
                .data(records)
                .build();
    }

    @Override
    public void insert(Table table, TableCsvDto data) throws ImageNotSupportedException, TableMalformedException {
        try {
            insertCsv(table, data);
        } catch (SQLException e) {
            log.error("could not insert data {}", e.getMessage());
            throw new TableMalformedException("could not insert data", e);
        }
    }

    @Override
    @Transactional
    public QueryResultDto selectAll(Long databaseId, Long tableId, Timestamp timestamp, Integer page, Integer size) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException {
        final Table table = findById(databaseId, tableId);
        try {
            if (table == null || table.getInternalName() == null) {
                log.error("Could not obtain the table internal name");
                throw new SQLException("Could not obtain the table internal name");
            }
            final DSLContext context = open(table.getDatabase());
            /* For versioning, but with jooq implementation better */
            if (table.getDatabase().getContainer().getImage().getDialect().equals("MARIADB")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT * FROM ");
                stringBuilder.append(table.getInternalName());
                if (timestamp != null) {
                    stringBuilder.append(" FOR SYSTEM_TIME AS OF TIMESTAMP'");
                    stringBuilder.append(timestamp.toLocalDateTime());
                    stringBuilder.append("'");
                }
                if (page != null && size != null) {
                    page = Math.abs(page);
                    size = Math.abs(size);
                    stringBuilder.append(" LIMIT ");
                    stringBuilder.append(size);
                    stringBuilder.append(" OFFSET ");
                    stringBuilder.append(page * size);
                }
                stringBuilder.append(";");
                return queryMapper.recordListToQueryResultDto(context.fetch(stringBuilder.toString()));
            } else {
                return queryMapper.recordListToQueryResultDto(context
                        .selectFrom(table.getInternalName())
                        .fetch());
            }
        } catch (SQLException e) {
            log.error("Could not find data: {}", e.getMessage());
            throw new DatabaseConnectionException(e);
        }
    }


}
