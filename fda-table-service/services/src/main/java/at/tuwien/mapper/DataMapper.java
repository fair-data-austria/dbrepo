package at.tuwien.mapper;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ImageNotSupportedException;
import org.assertj.core.util.Strings;
import org.hibernate.query.NativeQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mariadb.jdbc.MariaDbBlob;

import java.math.BigInteger;
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

    default QueryResultDto queryTableToQueryResultDto(NativeQuery<?> query, Table table) throws DateTimeException {
        final List<?> resultList = query.getResultList();
        final Iterator<?> iterator = resultList.iterator();
        final List<Map<String, Object>> result = new LinkedList<>();
        while (iterator.hasNext()) {
            /* map the result set to the columns through the stored metadata in the metadata database */
            int[] idx = new int[]{0};
            final Object[] data = (Object[]) iterator.next();
            final Map<String, Object> map = new HashMap<>();
            table.getColumns()
                    .forEach(column -> map.put(column.getName(), dataColumnToObject(data[idx[0]++], column)));
            result.add(map);
        }
        log.info("Selected {} records from table id {}", result.size(), table.getId());
        log.debug("table {} contains {} records", table, result.size());
        return QueryResultDto.builder()
                .result(result)
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

    default Object tableKeyObjectToObject(List<String> booleanColumns, TableInsertDto meta, String key, Object data) {
        /* null mapping */
        if (data == null || meta.getNullElement() == null || meta.getNullElement().isEmpty()
                || meta.getNullElement().isBlank() || data.equals(meta.getNullElement())) {
            return null;
        }
        /* boolean mapping */
        if (booleanColumns.size() == 0) {
            return data;
        }
        if (meta.getTrueElement() != null && booleanColumns.contains(key) && data.equals(meta.getTrueElement())) {
            return true;
        } else if (meta.getFalseElement() != null && booleanColumns.contains(key)
                && data.equals(meta.getFalseElement())) {
            return false;
        }
        return data;
    }

}
