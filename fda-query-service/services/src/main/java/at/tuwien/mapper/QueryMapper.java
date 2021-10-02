package at.tuwien.mapper;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.Query;
import org.jooq.Field;
import org.jooq.Record;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryResultDto queryToQueryResultDTO(Query query);

    Query queryDTOtoQuery(ExecuteQueryDto queryDTO);

    QueryDto queryToQueryDto(Query query);


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
                .id(Long.parseLong(data.get("id").toString()))
                .query((String) data.get("query"))
                .queryHash((String) data.get("query_hash"))
                .executionTimestamp((Timestamp) data.get("execution_timestamp"))
//                .queryNormalized((String) data.get("query_normalized")) FIXME
                .resultHash((String) data.get("result_hash"))
                .resultNumber(Long.parseLong(data.get("result_number").toString()))
                .build();
    }

    default List<Query> recordListToQueryList(List<Record> data) {
        return recordListToQueryResultDto(data, null)
                .getResult()
                .stream()
                .map(row -> Query.builder()
                        .id((Long.valueOf((Integer) row.get("id"))))
                        .query((String) row.get("query"))
                        .queryHash((String) row.get("query_hash"))
                        .queryNormalized((String) row.get("query_normalized"))
                        .resultHash((String) row.get("result_hash"))
                        .resultNumber((Integer) row.get("result_number"))
                        .executionTimestamp((Timestamp) row.get("execution_timestamp"))
                        .build())
                .collect(Collectors.toList());
    }


}
