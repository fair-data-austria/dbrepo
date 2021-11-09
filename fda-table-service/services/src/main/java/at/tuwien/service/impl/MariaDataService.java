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
import at.tuwien.utils.FileUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.validators.LineValidator;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        super(imageMapper, tableMapper);
        this.queryMapper = queryMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
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

    /**
     * Only supports CSVs where the first line is either a header line or not and the remaining lines contain data. A
     * comment block (e.g. {@literal https://sandbox.zenodo.org/api/files/1e8b8acb-7ef8-4437-8d96-7c15f6ef0ccc/2021-11-08T000000Z_N00009_G1_FN20ge_FRQ_WWV10.csv}
     * is not supported right now
     *
     * @param databaseId The database.
     * @param tableId    The table.
     * @param data       The null element and delimiter.
     * @throws TableNotFoundException
     * @throws ImageNotSupportedException
     * @throws DatabaseNotFoundException
     * @throws FileStorageException
     * @throws TableMalformedException
     */
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
            log.error("Failed to parse csv {}", e.getMessage());
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
            log.error("No database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }

    protected TableCsvDto readCsv(Table table, TableInsertDto data) throws IOException, CsvException,
            ArrayIndexOutOfBoundsException, TableMalformedException {
        log.debug("insert into table {} with params {}", table, data);
        if (data.getDelimiter() == null) {
            log.info("No delimiter provided, using comma ','");
            data.setDelimiter(',');
        }

        if (!FileUtils.isTestFile(data.getCsvLocation())) {
            if (!FileUtils.isUrl(data.getCsvLocation())) {
                data.setCsvLocation("/tmp/" + data.getCsvLocation());
            }
        } else {
            /* assume it is test file */
            data.setCsvLocation(data.getCsvLocation().substring(5));
        }
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(data.getDelimiter())
                .build();

        Reader fileReader;
        if (FileUtils.isUrl(data.getCsvLocation())) {
            /* source is remote file */
            fileReader = new BufferedReader(new InputStreamReader(URI.create(data.getCsvLocation()).toURL().openStream()));
        } else {
            /* source is local file */
            final MultipartFile multipartFile = new MockMultipartFile(data.getCsvLocation(),
                    Files.readAllBytes(Paths.get(data.getCsvLocation())));
            fileReader = new InputStreamReader(multipartFile.getInputStream());
        }

        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        List<String> headers = null;
        final LinkedList<List<String>> cells = new LinkedList<>();
        reader.readAll()
                .forEach(x -> cells.add(Arrays.asList(x)));
        log.trace("csv rows {}", cells.size());
        /* get header */
        if (data.getSkipHeader()) {
            headers = cells.get(0);
            log.debug("got headers {}", headers);
        }
        if (headers != null && headers.size() != table.getColumns().size()) {
            log.error("header size: {}, column size: {}", headers.size(), table.getColumns().size());
            throw new TableMalformedException("Header size is not the same as cell size, maybe wrong delimiter?");
        }
        final List<Map<String, Object>> records = new LinkedList<>();
        /* map to the map-list structure */
        for (int i = (data.getSkipHeader() ? 1 : 0); i < cells.size(); i++) {
            final Map<String, Object> record = new LinkedHashMap<>();
            final List<String> row = cells.get(i);
            for (int j = 0; j < table.getColumns().size(); j++) {
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
            log.error("Could not insert data {}", e.getMessage());
            throw new TableMalformedException("could not insert data", e);
        }
    }

    @Override
    @Transactional
    public QueryResultDto selectAll(@NonNull Long databaseId, @NonNull Long tableId, Instant timestamp,
                                    Long page, Long size) throws TableNotFoundException,
            DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException,
            TableMalformedException {
        if (page != null && page < 0) {
            throw new TableMalformedException("page cannot be lower than zero");
        }
        if (size != null && (size <= 0 || page == null)) {
            throw new TableMalformedException("size cannot be lower than zero or page is null");
        }
        final Table table = findById(databaseId, tableId);
        try {
            final DSLContext context = open(table.getDatabase());
            /* For versioning, but with jooq implementation better */
            if (table.getDatabase().getContainer().getImage().getDialect().equals("MARIADB")) {
                log.debug("MariaDB image, can do pagination!");
                StringBuilder stringBuilder = new StringBuilder()
                        .append("SELECT * FROM ")
                        .append(table.getInternalName());
                if (timestamp != null) {
                    stringBuilder.append(" FOR SYSTEM_TIME AS OF TIMESTAMP'")
                            .append(LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Vienna")))
                            .append("'");
                }
                if (page != null && size != null) {
                    stringBuilder.append(" LIMIT ")
                            .append(size)
                            .append(" OFFSET ")
                            .append(page * size)
                            .append(";");
                }
                return queryMapper.recordListToQueryResultDto(context.fetch(stringBuilder.toString()));
            } else {
                log.debug("Not MariaDB, can only provide legacy pagination");
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
