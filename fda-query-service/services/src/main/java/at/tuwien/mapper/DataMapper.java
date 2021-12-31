package at.tuwien.mapper;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.FileStorageException;
import at.tuwien.exception.ImageNotSupportedException;
import com.opencsv.CSVWriter;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mariadb.jdbc.MariaDbBlob;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DataMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataMapper.class);

    default Object dataColumnToObject(Object data, TableColumn column) throws DateTimeException {
        switch (column.getColumnType()) {
            case BLOB:
                log.trace("mapping {} to blob", data);
                return new MariaDbBlob((byte[]) data);
            case DATE:
                if (column.getDateFormat() == null) {
                    throw new IllegalArgumentException("Missing date format");
                }
                final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive() /* case insensitive to parse JAN and FEB */
                        .appendPattern(column.getDateFormat())
                        .toFormatter(Locale.ENGLISH);
                final LocalDate date = LocalDate.parse(String.valueOf(data), formatter);
                final Instant val = date.atStartOfDay(ZoneId.of("UTC"))
                        .toInstant();
                log.trace("mapping {} to date with format '{}' to value {}", data, column.getDateFormat(), val);
                return val;
            case ENUM:
            case TEXT:
            case STRING:
                log.trace("mapping {} to character array", data);
                return String.valueOf(data);
            case NUMBER:
                log.trace("mapping {} to non-decimal number", data);
                return new BigInteger(String.valueOf(data));
            case DECIMAL:
                log.trace("mapping {} to decimal number", data);
                return Double.valueOf(String.valueOf(data));
            case BOOLEAN:
                return Boolean.valueOf(String.valueOf(data));
            default:
                throw new IllegalArgumentException("Column type not known");
        }
    }

    default Object tableKeyObjectToObject(List<String> booleanColumns, String nullElement, String trueElement,
                                          String falseElement, String key, Object data) {
        /* null mapping */
        if (data == null || nullElement == null || nullElement.isEmpty() || nullElement.isBlank()
                || data.equals(nullElement)) {
            return null;
        }
        /* boolean mapping */
        if (booleanColumns.size() == 0) {
            return data;
        }
        if (booleanColumns.contains(key) && data.equals(trueElement)) {
            return true;
        } else if (booleanColumns.contains(key) && data.equals(falseElement)) {
            return false;
        }
        return data;
    }

    default Resource resultTableToResource(QueryResultDto result, Table table) throws FileStorageException {
        /* transform data */
        final List<String[]> data = tableQueryResultDtoToStringArrayList(table, result);
        /* generate csv */
        final String filename;
        try {
            filename = Arrays.toString(MessageDigest.getInstance("MD5")
                    .digest(Instant.now()
                            .toString()
                            .getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new FileStorageException("Algorithm md5 not available", e);
        }
        /* create writers and temporary files */
        final File file;
        final FileWriter outputfile;
        try {
            file = File.createTempFile(filename, ".tmp", new File(System.getProperty("java.io.tmpdir")));
            outputfile = new FileWriter(file, true);
        } catch (IOException e) {
            throw new FileStorageException("Temporary file not instantiable", e);
        }
        final CSVWriter writer = new CSVWriter(outputfile, table.getSeparator(),
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        /* write to file with column names */
        writer.writeAll(data);
        final ByteArrayResource resource;
        try {
            writer.close();
            resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new FileStorageException("Could not save the temporary file", e);
        }
        return resource;
    }

    default List<String[]> tableQueryResultDtoToStringArrayList(Table table, QueryResultDto data) {
        final List<String[]> rows = new LinkedList<>();
        final String[] headers = table.getColumns()
                .stream()
                .map(TableColumn::getName)
                .toArray(String[]::new);
        log.debug("mapped csv headers {}", Arrays.toString(headers));
        rows.add(headers);
        data.getResult()
                .forEach(row -> rows.add(Arrays.stream(row.values()
                                .toArray())
                        .map(String::valueOf)
                        .toArray(String[]::new)));
        log.debug("mapped csv rows {}", rows.size() - 1);
        /* map null */
        /* map boolean */
        return rows;
    }

}
