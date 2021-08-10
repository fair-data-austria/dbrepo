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

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryResultDto queryToQueryResultDTO(Query query);

    Query queryDTOtoQuery(ExecuteQueryDto queryDTO);

    QueryDto queryToQueryDto(Query query);


    default QueryResultDto recordListToQueryResultDto(List<Record> data) {
        final List<Map<String, Object>> result = new LinkedList<>();
        for (Record record : data) {
            final Map<String, Object> map = new HashMap<>();
            for (Field<?> column : record.fields()) {
                map.put(column.getName(), record.get(column.getName()));
            }
            result.add(map);
        }
        return QueryResultDto.builder()
                .result(result)
                .build();
    }

    default List<Query> recordListToQueryList(List<Record> data) {
        QueryResultDto queryResultDto = recordListToQueryResultDto(data);
        List<Query> queries = new ArrayList<>();
        int i=0;
        for (Map<String, Object> m :  queryResultDto.getResult()) {
            queries.add(Query.builder()
                    .id((Long.valueOf((Integer) m.get("id"))))
                    .query((String)m.get("query"))
                    .queryHash((String)m.get("query_hash"))
                    .queryNormalized((String)m.get("query_normalized"))
                    .resultHash((String)m.get("result_hash"))
                    .resultNumber((Integer)m.get("result_number"))
                    .executionTimestamp((Timestamp)m.get("execution_timestamp"))
                    .build());
        }
        return queries;
    }



}
