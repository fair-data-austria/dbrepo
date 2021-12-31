package at.tuwien.mapper;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryBriefDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.QueryStoreException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mariadb.jdbc.MariaDbBlob;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(QueryMapper.class);

    Query queryDtotoQuery(QueryDto data);

    QueryDto queryToQueryDto(Query data);

    QueryBriefDto queryToQueryBriefDto(Query data);

    @Mappings({
            @Mapping(source = "query", target = "queryNormalized")
    })
    QueryDto executeQueryDtoToQueryDto(ExecuteQueryDto data);

    default List<QueryDto> resultListToQueryStoreQueryList(List<?> data) throws QueryStoreException {
        final List<QueryDto> queries = new LinkedList<>();
        final Iterator<?> iterator = data.iterator();
        while (iterator.hasNext()) {
            final Object[] row = (Object[]) iterator.next();
            queries.add(QueryDto.builder()
                    .id(Long.valueOf(String.valueOf(row[0])))
                    .doi(String.valueOf(row[1]))
                    .title(String.valueOf(row[2]))
                    .query(String.valueOf(row[3]))
                    .queryHash(String.valueOf(row[4]))
                    .executionTimestamp(Instant.parse(String.valueOf(row[5])))
                    .resultHash(String.valueOf(row[6]))
                    .resultNumber(Long.valueOf(String.valueOf(row[7])))
                    .created(Instant.parse(String.valueOf(row[8])))
                    .build());
        }
        return queries;
    }

    default QueryResultDto resultListToQueryResultDto(Table table, List<?> result) {
        final Iterator<?> iterator = result.iterator();
        final List<Map<String, Object>> resultList = new LinkedList<>();
        while (iterator.hasNext()) {
            /* map the result set to the columns through the stored metadata in the metadata database */
            int[] idx = new int[]{0};
            final Object[] data = (Object[]) iterator.next();
            final Map<String, Object> map = new HashMap<>();
            table.getColumns()
                    .forEach(column -> map.put(column.getName(), dataColumnToObject(data[idx[0]++], column)));
            resultList.add(map);
        }
        log.info("Selected {} records from table id {}", resultList.size(), table.getId());
        log.debug("table {} contains {} records", table, resultList.size());
        return QueryResultDto.builder()
                .result(resultList)
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

    default QueryDto queryResultDtoToQueryDto(QueryResultDto data, ExecuteQueryDto metadata) throws QueryStoreException {
        try {
            return QueryDto.builder()
                    .title(metadata.getTitle())
                    .description(metadata.getDescription())
                    .resultNumber(Long.parseLong(String.valueOf(data.getResult().size())))
                    .resultHash(Arrays.toString(MessageDigest.getInstance("SHA256")
                            .digest(data.getResult()
                                    .toString()
                                    .getBytes())))
                    .executionTimestamp(Instant.now())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new QueryStoreException("Failed to find sha256 algorithm", e);
        }
    }

}
