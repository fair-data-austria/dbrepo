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

    @Named("internalMapping")
    default String nameToInternalName(String data) {
        if (data == null || data.length() == 0) {
            return data;
        }
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(data).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    default String tableToRawFindAllQuery(Table table, Instant timestamp, Long size, Long page)
            throws ImageNotSupportedException {
        /* param check */
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        final StringBuilder query = new StringBuilder("SELECT * FROM `")
                .append(nameToInternalName(table.getName()))
                .append("` FOR SYSTEM_TIME AS OF TIMESTAMP'")
                .append(LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Vienna")))
                .append("'");
        if (size != null && page != null) {
            log.debug("pagination size/limit of {}", size);
            query.append(" LIMIT ")
                    .append(size);
            log.debug("pagination page/offset of {}", page);
            query.append(" OFFSET ")
                    .append(page * size)
                    .append(";");

        }
        log.debug("create table query built with {} columns and system versioning", table.getColumns().size());
        log.trace("raw create table query: [{}]", query);
        return query.toString();
    }

    default QueryResultDto queryTableToQueryResultDto(List<?> result, Table table) throws DateTimeException {
        final Iterator<?> iterator = result.iterator();
        final List<Map<String, Object>> queryResult = new LinkedList<>();
        while (iterator.hasNext()) {
            /* map the result set to the columns through the stored metadata in the metadata database */
            int[] idx = new int[]{0};
            final Object[] data = (Object[]) iterator.next();
            final Map<String, Object> map = new HashMap<>();
            table.getColumns()
                    .forEach(column -> map.put(column.getName(), dataColumnToObject(data[idx[0]++], column)));
            queryResult.add(map);
        }
        log.info("Selected {} records from table id {}", queryResult.size(), table.getId());
        log.debug("table {} contains {} records", table, queryResult.size());
        return QueryResultDto.builder()
                .result(queryResult)
                .build();
    }

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

    default InsertTableRawQuery tableTableCsvDtoToRawInsertQuery(Table table, TableCsvDto data) {
        final int[] idx = {1} /* this needs to be >0 */;
        /* parameterized query for prepared statement */
        final StringBuilder query = new StringBuilder("INSERT INTO `")
                .append(table.getInternalName())
                .append("` (")
                .append(table.getColumns()
                        .stream()
                        .filter(column -> !column.getAutoGenerated())
                        .map(column -> "`" + column.getInternalName() + "`")
                        .collect(Collectors.joining(",")))
                .append(") VALUES ");
        for (int i = 0; i < data.getData().size(); i++) {
            query.append(i > 0 ? ", " : "")
                    .append("(?")
                    .append(idx[0]++)
                    .append(")");
        }
        query.append(";");
        /* values for prepared statement */
        final List<Collection<Object>> values = data.getData()
                .stream()
                .map(Map::values)
                .collect(Collectors.toList());
        /* debug */
        log.trace("raw create table query: [" + query + "]");
        return InsertTableRawQuery.builder()
                .query(query.toString())
                .values(values)
                .build();
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
                .forEach(row -> rows.add(row.values().toArray(new String[0])));
        log.debug("mapped csv rows {}", rows.size() - 1);
        /* map null */
        /* map boolean */
        return rows;
    }

}
