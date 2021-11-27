package at.tuwien.mapper;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import org.jooq.Field;
import org.jooq.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryResultDto queryToQueryResultDto(Query data);

    Query executeQueryDtoToQuery(ExecuteQueryDto data);

    Query queryDtotoQuery(QueryDto data);

    QueryDto queryToQueryDto(Query data);

    @Mappings({
            @Mapping(source = "query", target = "query"),
            @Mapping(source = "query", target = "queryNormalized")
    })
    QueryDto executeQueryDtoToQueryDto(ExecuteQueryDto data);

    default QueryResultDto recordListToQueryResultDto(List<Record> data, Long queryId) {
        final List<Map<String, Object>> result = new LinkedList<>();
        for (Record record : data) {
            final Map<String, Object> map = new HashMap<>();
            for (Field<?> column : record.fields()) {
                map.put(column.getName(), record.get(column.getName()));
            }
            result.add(map);
        }
        return QueryResultDto.builder()
                .id(queryId)
                .result(result)
                .build();
    }

    default QueryDto recordToQueryDto(Record data) {
        return QueryDto.builder()
                .id(Long.parseLong(String.valueOf(data.get("id"))))
                .query(String.valueOf(data.get("query")))
                .queryHash(String.valueOf(data.get("query_hash")))
                .executionTimestamp(objectToInstant(data.get("execution_timestamp")))
                .resultHash(String.valueOf(data.get("result_hash")))
                .resultNumber(Long.parseLong(String.valueOf(data.get("result_number"))))
                .build();
    }

    default List<Query> recordListToQueryList(List<Record> data) {
        return recordListToQueryResultDto(data, null)
                .getResult()
                .stream()
                .map(row -> Query.builder()
                        .id(Long.valueOf(String.valueOf(row.get("id"))))
                        .resultHash(String.valueOf(row.get("result_hash")))
                        .resultNumber(Long.valueOf(String.valueOf(row.get("result_number"))))
                        .executionTimestamp(objectToInstant(row.get("execution_timestamp")))
                        .build())
                .collect(Collectors.toList());
    }

    default Instant objectToInstant(Object data) {
        if (data == null) {
            return null;
        }
        return Instant.parse(data.toString());
    }

}
