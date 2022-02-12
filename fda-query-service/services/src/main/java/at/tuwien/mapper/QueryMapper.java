package at.tuwien.mapper;

import at.tuwien.api.database.query.*;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.querystore.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mariadb.jdbc.MariaDbBlob;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(QueryMapper.class);

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

    @Mappings({
            @Mapping(source = "query", target = "statement")
    })
    ExecuteStatementDto queryDtoToExecuteStatementDto(QueryDto data);

    ExecuteStatementDto saveStatementDtoToExecuteStatementDto(SaveStatementDto data);

    QueryDto queryToQueryDto(Query data);

    List<QueryDto> queryListToQueryDtoList(List<Query> data);

    default String tableToRawCountAllQuery(Table table, Instant timestamp) throws ImageNotSupportedException {
        /* param check */
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        return "SELECT COUNT(*) FROM `" + nameToInternalName(table.getName()) +
                "` FOR SYSTEM_TIME AS OF TIMESTAMP'" +
                LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Vienna")) +
                "';";
    }

    default String tableToRawFindAllQuery(Table table, Instant timestamp, Long size, Long page)
            throws ImageNotSupportedException {
        /* param check */
        if (!table.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
            log.debug("no timestamp provided, default to {}", timestamp);
        } else {
            log.debug("timestamp provided {}", timestamp);
        }
        final int[] idx = new int[]{0};
        final StringBuilder query = new StringBuilder("SELECT ");
        table.getColumns()
                .forEach(column -> query.append(idx[0]++ > 0 ? "," : "")
                        .append("`")
                        .append(column.getInternalName())
                        .append("`"));
        query.append(" FROM `")
                .append(nameToInternalName(table.getName()))
                .append("` FOR SYSTEM_TIME AS OF TIMESTAMP'")
                .append(LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Vienna")))
                .append("'");
        if (size != null && page != null) {
            log.trace("pagination size/limit of {}", size);
            query.append(" LIMIT ")
                    .append(size);
            log.trace("pagination page/offset of {}", page);
            query.append(" OFFSET ")
                    .append(page * size)
                    .append(";");

        }
        log.trace("raw select table query: [{}]", query);
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
        log.trace("table {} contains {} records", table, queryResult.size());
        return QueryResultDto.builder()
                .result(queryResult)
                .build();
    }

    default QueryResultDto resultListToQueryResultDto(List<TableColumn> columns, List<?> result) {
        final Iterator<?> iterator = result.iterator();
        final List<Map<String, Object>> resultList = new LinkedList<>();
        while (iterator.hasNext()) {
            /* map the result set to the columns through the stored metadata in the metadata database */
            int[] idx = new int[]{0};
            final Object[] data = (Object[]) iterator.next();
            final Map<String, Object> map = new HashMap<>();
            columns
                    .forEach(column -> map.put(column.getName(),
                            dataColumnToObject(data[idx[0]++], column)));
            resultList.add(map);
        }
        return QueryResultDto.builder()
                .result(resultList)
                .build();
    }

    @Transactional(readOnly = true)
    default Object dataColumnToObject(Object data, TableColumn column) throws DateTimeException {
        if (data == null) {
            return null;
        }
        log.trace("map data {} to table column {}", data, column);
        switch (column.getColumnType()) {
            case BLOB:
                log.trace("mapping {} to blob", data);
                return new MariaDbBlob((byte[]) data);
            case DATE:
                if (column.getDateFormat() == null) {
                    log.error("Missing date format for column {} of table {}", column.getId(), column.getTable().getId());
                    throw new IllegalArgumentException("Missing date format");
                }
                final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive() /* case insensitive to parse JAN and FEB */
                        .appendPattern(column.getDateFormat().getUnixFormat())
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
}
