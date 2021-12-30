package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.TableNotFoundException;
import at.tuwien.mapper.DataMapper;
import at.tuwien.service.TableService;
import at.tuwien.service.TextDataService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsvServiceImpl implements TextDataService {

    private final DataMapper dataMapper;
    private final TableService tableService;

    @Autowired
    public CsvServiceImpl(DataMapper dataMapper, TableService tableService) {
        this.dataMapper = dataMapper;
        this.tableService = tableService;
    }

    @Override
    public TableCsvDto read(Long databaseId, Long tableId, TableInsertDto data) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException {
        /* find */
        final Table table = tableService.findById(databaseId, tableId);
        /* set default parameters */
        log.trace("insert into table {} with params {}", table, data);
        if (data.getDelimiter() == null) {
            log.warn("No delimiter provided, using comma ','");
            data.setDelimiter(',');
        }
        /* correct the file path */
        boolean isClassPathFile = false;
        if (FileUtils.isTestFile(data.getCsvLocation())) {
            isClassPathFile = true;
            log.trace("read test file from {}", data.getCsvLocation().substring(5));
            data.setCsvLocation(data.getCsvLocation().substring(5));
        } else if (FileUtils.isUrl(data.getCsvLocation())) {
            log.trace("read remote file from {}", data.getCsvLocation());
        } else {
            log.trace("read prod file from /tmp/{}", data.getCsvLocation());
            data.setCsvLocation("/tmp/" + data.getCsvLocation());
        }
        /* retrieve data */
        final CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(data.getDelimiter())
                .build();
        /* read the file */
        Reader fileReader;
        if (FileUtils.isUrl(data.getCsvLocation())) {
            /* source is remote file */
            log.trace("read file from url {}", data.getCsvLocation());
            fileReader = new BufferedReader(new InputStreamReader(URI.create(data.getCsvLocation()).toURL()
                    .openStream()));
        } else {
            MultipartFile multipartFile;
            log.trace("generate multipart file for location {}, classpath (y/n) {}", data.getCsvLocation(), isClassPathFile ? 'y' : 'n');
            if (!isClassPathFile) {
                /* source is local file, read from external /tmp path */
                multipartFile = new MockMultipartFile(data.getCsvLocation(),
                        Files.readAllBytes(Paths.get(data.getCsvLocation())));
            } else {
                /* source is in class path */
                final InputStream stream = new ClassPathResource(data.getCsvLocation()).getInputStream();
                multipartFile = new MockMultipartFile(data.getCsvLocation(),
                        stream.readAllBytes());
            }
            fileReader = new InputStreamReader(multipartFile.getInputStream());
        }
        /* parse the csv */
        final CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)
                .build();
        final List<List<String>> rows = new LinkedList<>();
        reader.readAll()
                .forEach(x -> rows.add(new ArrayList<>(List.of(x))));
        log.trace("csv rows {}", rows.size());
        /* generic header, ref issue #95 */
        List<String> headers = TableUtils.fill(0, rows.get(0).size());
        if (data.getSkipHeader()) {
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
        for (int k = (data.getSkipHeader() ? 1 : 0); k < rows.size(); k++) {
            final Map<String, Object> record = new LinkedHashMap<>();
            final List<String> row = rows.get(k);
            for (int i = 0; i < table.getColumns().size(); i++) {
                record.put(table.getColumns().get(i).getInternalName(), row.get(i));
            }
            record.replaceAll((key, value) -> dataMapper.tableKeyObjectToObject(booleanColumns, data, key, value));
            records.add(record);
        }

        return TableCsvDto.builder()
                .data(records)
                .build();
    }

}
