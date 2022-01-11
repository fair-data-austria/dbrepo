package at.tuwien.service.impl;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import at.tuwien.exception.*;
import at.tuwien.mapper.DataMapper;
import at.tuwien.service.CommaValueService;
import at.tuwien.service.QueryService;
import at.tuwien.service.TableService;
import at.tuwien.utils.FileUtils;
import at.tuwien.utils.TableUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommaValueServiceImpl implements CommaValueService {

    private final DataMapper dataMapper;
    private final QueryService queryService;
    private final TableService tableService;

    @Autowired
    public CommaValueServiceImpl(DataMapper dataMapper, QueryService queryService, TableService tableService) {
        this.dataMapper = dataMapper;
        this.queryService = queryService;
        this.tableService = tableService;
    }

    @Override
    @Transactional
    public TableCsvDto read(Long databaseId, Long tableId, String location) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException {
        return read(databaseId, tableId, location, ',', false, null, "0", "1");
    }

    @Override
    @Transactional
    public TableCsvDto read(Long databaseId, Long tableId, String location, Character separator, Boolean skipHeader, String nullElement,
                            String falseElement, String trueElement) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException {
        /* find */
        final Table table = tableService.find(databaseId, tableId);
        /* set default parameters */
        log.trace("insert into table {} with separator {} and csv location {} and skip header {}", table, separator, location, skipHeader);
        /* correct the file path */
        boolean isClassPathFile = false;
        if (FileUtils.isTestFile(location)) {
            isClassPathFile = true;
            log.trace("read test file from {}", location.substring(5));
            location = location.substring(5);
        } else if (FileUtils.isUrl(location)) {
            log.trace("read remote file from {}", location);
        } else {
            log.trace("read prod file from /tmp/{}", location);
            location = "/tmp/" + location;
        }
        /* retrieve data */
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();
        /* read the file */
        Reader fileReader;
        if (FileUtils.isUrl(location)) {
            /* source is remote file */
            log.trace("read file from url {}", location);
            try {
                fileReader = new BufferedReader(new InputStreamReader(URI.create(location)
                        .toURL()
                        .openStream()));
            } catch (IOException e) {
                log.error("Failed to read from url");
                throw new FileStorageException("Failed to read from url", e);
            }
        } else {
            MultipartFile multipartFile;
            log.trace("generate multipart file for location {}, classpath (y/n) {}", location, isClassPathFile ? 'y' : 'n');
            if (!isClassPathFile) {
                /* source is local file, read from external /tmp path */
                try {
                    multipartFile = new MockMultipartFile(location,
                            Files.readAllBytes(Paths.get(location)));
                } catch (IOException e) {
                    log.error("Failed to read from local file");
                    throw new FileStorageException("Failed to read from local file", e);
                }
            } else {
                /* source is in class path */
                final InputStream stream;
                try {
                    stream = new ClassPathResource(location)
                            .getInputStream();
                    multipartFile = new MockMultipartFile(location,
                            stream.readAllBytes());
                } catch (IOException e) {
                    log.error("Failed to read from class path");
                    throw new FileStorageException("Failed to read from class path", e);
                }
            }
            try {
                fileReader = new InputStreamReader(multipartFile.getInputStream());
            } catch (IOException e) {
                log.error("Failed to setup input stream reader");
                throw new FileStorageException("Failed to setup input stream reader", e);
            }
        }
        /* parse the csv */
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        final List<List<String>> rows = new LinkedList<>();
        try {
            reader.readAll()
                    .forEach(x -> rows.add(new ArrayList<>(List.of(x))));
        } catch (IOException | CsvException e) {
            log.error("Failed to read rows");
            throw new FileStorageException("Failed to read rows", e);
        }
        log.trace("csv rows {}", rows.size());
        List<String> headers;
        if (!skipHeader) {
            /* generic header, ref issue #95 */
            headers = TableUtils.genericHeaders(rows.get(0).size());
        } else {
            /* get header */
            headers = rows.get(0);
            log.trace("csv headers {}", headers);
        }
        /* start building the data structure */
        final List<Map<String, Object>> records = new LinkedList<>();
        final List<String> booleanColumns = table.getColumns()
                .stream()
                .filter(c -> c.getColumnType().equals(TableColumnType.BOOLEAN))
                .map(TableColumn::getInternalName)
                .collect(Collectors.toList());
        /* map to the map-list structure */
        for (int k = (skipHeader ? 1 : 0); k < rows.size(); k++) {
            final Map<String, Object> record = new LinkedHashMap<>();
            final List<String> row = rows.get(k);
            for (int i = 0; i < table.getColumns().size(); i++) {
                record.put(table.getColumns().get(i).getInternalName(), row.get(i));
            }
            record.replaceAll((key, value) -> dataMapper.tableKeyObjectToObject(booleanColumns, nullElement,
                    trueElement, falseElement, key, value));
            records.add(record);
        }
        return TableCsvDto.builder()
                .data(records)
                .build();
    }

    @Override
    @Transactional
    public InputStreamResource export(Long databaseId, Long tableId, Instant timestamp) throws TableNotFoundException,
            DatabaseNotFoundException, DatabaseConnectionException, TableMalformedException, ImageNotSupportedException,
            PaginationException, FileStorageException {
        /* find */
        final Table table = tableService.find(databaseId, tableId);
        final QueryResultDto result = queryService.findAll(databaseId, tableId, timestamp, null, null);
        /* write */
        final Resource csv = dataMapper.resultTableToResource(result, table);
        final InputStreamResource resource;
        try {
            resource = new InputStreamResource(csv.getInputStream());
        } catch (IOException e) {
            log.error("Failed to map resource");
            throw new FileStorageException("Failed to map resource", e);
        }
        log.trace("produced csv {}", csv);
        return resource;
    }

    @Override
    @Transactional
    public InputStreamResource export(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException,
            PaginationException {
        return export(databaseId, tableId, Instant.now());
    }

}
