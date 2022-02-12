package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.DataMapper;
import at.tuwien.service.CommaValueService;
import at.tuwien.service.ContainerService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CommaValueServiceImpl implements CommaValueService {

    private final DataMapper dataMapper;
    private final TableService tableService;
    private final ContainerService containerService;

    @Autowired
    public CommaValueServiceImpl(DataMapper dataMapper, TableService tableService, ContainerService containerService) {
        this.dataMapper = dataMapper;
        this.tableService = tableService;
        this.containerService = containerService;
    }

    @Override
    @Deprecated
    @Transactional
    public TableCsvDto read(Long containerId, Long databaseId, Long tableId, String location) throws TableNotFoundException,
            DatabaseNotFoundException, FileStorageException, ContainerNotFoundException {
        return read(containerId, databaseId, tableId, location, ',', 0L, null, "0", "1");
    }

    @Override
    @Deprecated
    @Transactional
    public TableCsvDto read(Long containerId, Long databaseId, Long tableId, String location, Character separator,
                            Long skipLines, String nullElement, String falseElement, String trueElement)
            throws TableNotFoundException, DatabaseNotFoundException, FileStorageException, ContainerNotFoundException {
        /* find */
        final Container container = containerService.find(containerId);
        final Table table = tableService.find(databaseId, tableId);
        /* set default parameters */
        log.trace("insert into table {} with separator {} and csv location {} and skip lines {}", table, separator, location, skipLines);
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
        final long readStart = System.currentTimeMillis();
        log.debug("started csv parsing, {}", readStart);
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        List<List<Object>> rows = new LinkedList<>();
        String[] line;
        long idx = 0L;
        try {
            while ((line = reader.readNext()) != null && idx++ >= 0) {
                if (skipLines != null && idx <= skipLines) {
                    continue;
                }
                rows.add(Stream.of(line)
                        .collect(Collectors.toList()));
            }
        } catch (IOException | CsvException e) {
            log.error("Failed to read rows");
            throw new FileStorageException("Failed to read rows", e);
        }
        rows = rows.stream()
                .peek(row -> row.replaceAll(column -> dataMapper.tableColumnToObject(column, nullElement,
                        trueElement, falseElement)))
                .collect(Collectors.toList());
        log.debug("ended csv parsing, {}", System.currentTimeMillis());
        log.info("Parsed csv in {} ms", System.currentTimeMillis() - readStart);
        log.debug("csv rows {}", rows.size());
        List<String> headers;
        if (skipLines == 0) {
            /* generic header, ref issue #95 */
            headers = TableUtils.genericHeaders(rows.get(0).size());
        } else {
            /* get header */
            headers = rows.get(0)
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            log.trace("csv headers {}", headers);
        }
        log.debug("parsed {} records", rows.size());
        return TableCsvDto.builder()
//                .data(rows)
                .build();
    }

//    @Override
//    @Transactional
//    public InputStreamResource export(Long containerId, Long databaseId, Long tableId, Instant timestamp)
//            throws TableNotFoundException, DatabaseNotFoundException, DatabaseConnectionException,
//            TableMalformedException, ImageNotSupportedException, PaginationException, FileStorageException,
//            ContainerNotFoundException {
//        /* find */
//        final Container container = containerService.find(containerId);
//        final Table table = tableService.find(databaseId, tableId);
//        final QueryResultDto result = queryService.findAll(containerId, databaseId, tableId, timestamp, null, null);
//        /* write */
//        final Resource csv = dataMapper.resultTableToResource(result, table);
//        final InputStreamResource resource;
//        try {
//            resource = new InputStreamResource(csv.getInputStream());
//        } catch (IOException e) {
//            log.error("Failed to map resource");
//            throw new FileStorageException("Failed to map resource", e);
//        }
//        log.trace("produced csv {}", csv);
//        return resource;
//    }

//    @Override
//    @Transactional
//    public InputStreamResource export(Long containerId, Long databaseId, Long tableId) throws TableNotFoundException,
//            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException,
//            FileStorageException, PaginationException, ContainerNotFoundException {
//        return export(containerId, databaseId, tableId, Instant.now());
//    }

}
